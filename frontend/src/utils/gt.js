'v0.4.6 Geetest Inc.'
;(function (window) {
  'use strict'
  if (typeof window === 'undefined') {
    throw new Error('Geetest requires browser environment')
  }

  const document = window.document
  const Math = window.Math
  const head = document.getElementsByTagName('head')[0]

  function _Object(obj) {
    //eslint-disable-next-line no-invalid-this
    this._obj = obj
  }

  _Object.prototype = {
    _each: function (process) {
      const _obj = this._obj
      for (let k in _obj) {
        if (_obj.hasOwnProperty(k)) {
          process(k, _obj[k])
        }
      }
      return this
    }
  }

  function Config(config) {
    const self = this
    new _Object(config)._each(function (key, value) {
      self[key] = value
    })
  }

  Config.prototype = {
    api_server: 'api.geetest.com',
    protocol: 'http://',
    typePath: '/gettype.php',
    fallback_config: {
      slide: {
        static_servers: ['static.geetest.com', 'dn-staticdown.qbox.me'],
        type: 'slide',
        slide: '/static/js/geetest.0.0.0.js'
      },
      fullpage: {
        static_servers: ['static.geetest.com', 'dn-staticdown.qbox.me'],
        type: 'fullpage',
        fullpage: '/static/js/fullpage.0.0.0.js'
      }
    },
    _get_fallback_config: function () {
      const self = this
      if (isString(self.type)) {
        return self.fallback_config[self.type]
      } else if (self.new_captcha) {
        return self.fallback_config.fullpage
      } else {
        return self.fallback_config.slide
      }
    },
    _extend: function (obj) {
      const self = this
      new _Object(obj)._each(function (key, value) {
        self[key] = value
      })
    }
  }
  const isNumber = function (value) {
    return typeof value === 'number'
  }
  const isString = function (value) {
    return typeof value === 'string'
  }
  const isBoolean = function (value) {
    return typeof value === 'boolean'
  }
  const isObject = function (value) {
    return typeof value === 'object' && value !== null
  }
  const isFunction = function (value) {
    return typeof value === 'function'
  }

  const callbacks = {}
  const status = {}

  const random = function () {
    const n = 10000
    return parseInt(Math.random() * n) + new Date().valueOf()
  }

  const loadScript = function (url, cb) {
    const script = document.createElement('script')
    script.charset = 'UTF-8'
    script.async = true

    script.onerror = function () {
      cb(true)
    }
    let loaded = false
    script.onload = script.onreadystatechange = function () {
      if (!loaded && (!script.readyState || 'loaded' === script.readyState || 'complete' === script.readyState)) {
        loaded = true
        setTimeout(function () {
          cb(false)
        }, 0)
      }
    }
    script.src = url
    head.appendChild(script)
  }

  const normalizeDomain = function (domain) {
    // special domain: uems.sysu.edu.cn/jwxt/geetest/
    // return domain.replace(/^https?:\/\/|\/.*$/g, ''); uems.sysu.edu.cn
    return domain.replace(/^https?:\/\/|\/$/g, '') // uems.sysu.edu.cn/jwxt/geetest
  }
  const normalizePath = function (path) {
    path = path.replace(/\/+/g, '/')
    if (path.indexOf('/') !== 0) {
      path = '/' + path
    }
    return path
  }
  const normalizeQuery = function (query) {
    if (!query) {
      return ''
    }
    let q = '?'
    new _Object(query)._each(function (key, value) {
      if (isString(value) || isNumber(value) || isBoolean(value)) {
        q = q + encodeURIComponent(key) + '=' + encodeURIComponent(value) + '&'
      }
    })
    if (q === '?') {
      q = ''
    }
    return q.replace(/&$/, '')
  }
  const makeURL = function (protocol, domain, path, query) {
    domain = normalizeDomain(domain)

    let url = normalizePath(path) + normalizeQuery(query)
    if (domain) {
      url = protocol + domain + url
    }

    return url
  }

  const load = function (protocol, domains, path, query, cb) {
    const tryRequest = function (at) {
      const url = makeURL(protocol, domains[at], path, query)
      loadScript(url, function (err) {
        if (err) {
          if (at >= domains.length - 1) {
            cb(true)
          } else {
            tryRequest(at + 1)
          }
        } else {
          cb(false)
        }
      })
    }
    tryRequest(0)
  }

  const jsonp = function (domains, path, config, callback) {
    if (isObject(config.getLib)) {
      config._extend(config.getLib)
      callback(config)
      return
    }
    if (config.offline) {
      callback(config._get_fallback_config())
      return
    }

    const cb = 'geetest_' + random()
    window[cb] = function (data) {
      if (data.status === 'success') {
        callback(data.data)
      } else if (!data.status) {
        callback(data)
      } else {
        callback(config._get_fallback_config())
      }
      window[cb] = undefined
      try {
        delete window[cb]
      } catch (e) {
        throw e
      }
    }
    load(
      config.protocol,
      domains,
      path,
      {
        gt: config.gt,
        callback: cb
      },
      function (err) {
        if (err) {
          callback(config._get_fallback_config())
        }
      }
    )
  }

  const throwError = function (errorType, config) {
    const errors = {
      networkError: '网络错误',
      gtTypeError: 'gt字段不是字符串类型'
    }
    if (typeof config.onError === 'function') {
      config.onError(errors[errorType])
    } else {
      throw new Error(errors[errorType])
    }
  }

  const detect = function () {
    return window.Geetest || document.getElementById('gt_lib')
  }

  if (detect()) {
    status.slide = 'loaded'
  }

  window.initGeetest = function (userConfig, callback) {
    const config = new Config(userConfig)

    if (userConfig.https) {
      config.protocol = 'https://'
    } else if (!userConfig.protocol) {
      config.protocol = window.location.protocol + '//'
    }

    // for KFC
    if (userConfig.gt === '050cffef4ae57b5d5e529fea9540b0d1' || userConfig.gt === '3bd38408ae4af923ed36e13819b14d42') {
      config.apiserver = 'yumchina.geetest.com/' // for old js
      config.api_server = 'yumchina.geetest.com'
    }

    if (isObject(userConfig.getType)) {
      config._extend(userConfig.getType)
    }
    jsonp([config.api_server || config.apiserver], config.typePath, config, function (newConfig) {
      const type = newConfig.type
      const init = function () {
        config._extend(newConfig)
        callback(new window.Geetest(config))
      }

      callbacks[type] = callbacks[type] || []
      const s = status[type] || 'init'
      if (s === 'init') {
        status[type] = 'loading'

        callbacks[type].push(init)

        load(
          config.protocol,
          newConfig.static_servers || newConfig.domains,
          newConfig[type] || newConfig.path,
          null,
          function (err) {
            if (err) {
              status[type] = 'fail'
              throwError('networkError', config)
            } else {
              status[type] = 'loaded'
              const cbs = callbacks[type]
              for (let i = 0, len = cbs.length; i < len; i = i + 1) {
                let cb = cbs[i]
                if (isFunction(cb)) {
                  cb()
                }
              }
              callbacks[type] = []
            }
          }
        )
      } else if (s === 'loaded') {
        init()
      } else if (s === 'fail') {
        throwError('networkError', config)
      } else if (s === 'loading') {
        callbacks[type].push(init)
      }
    })
  }
})(window)
