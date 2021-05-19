/**
 * 验证URL链接
 * @param  {string} str_url 需要验证的URL
 * @return {boolean}        返回是否URL合法
 */
export function isURL(str_url: string): boolean {
  let strRegex =
    '^((https|http|ftp|rtsp|mms)?://)' +
    "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + //ftp的user@
    '(([0-9]{1,3}.){3}[0-9]{1,3}' + // IP形式的URL- 199.194.52.184
    '|' + // 允许IP和DOMAIN（域名）
    "([0-9a-z_!~*'()-]+.)*" + // 域名- www.
    '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' + // 二级域名
    '[a-z]{2,6})' + // first level domain- .com or .museum
    '(:[0-9]{1,4})?' + // 端口- :80
    '((/?)|' + // a slash isn't required if there is no file name
    "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"
  let re = new RegExp(strRegex)
  return re.test(str_url)
}

/**
 * 删除数据
 * @param vueObj
 * @param url
 * @param id
 */

/*export function deleteObject(vueObj, url, id) {
  if (id !== null && id !== '') {
    vueObj
      .$confirm('是否确定要删除此数据？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      .then(() => {
        $ajax(
          {
            url: url + '/' + id,
            method: 'delete',
            headers: {
              'X-CSRF-TOKEN': $("meta[name='X-CSRF-TOKEN']").prop('content')
            }
          },
          data => {
            if (data.state) {
              vueObj.$message.success(data.message)
              setTimeout(function () {
                location.reload()
              }, 3000)
            } else {
              vueObj.$message.error(data.message)
            }
          },
          xhr => {
            vueObj.$message.error(xhr.responseJSON.messageError.defaultMessage)
          }
        )
      })
  }
}*/

/**
 * 去除字符串中所有空格、制表符、换行符
 * @returns {string | void | *}
 */
export function trim(string: string): string {
  return string.replace(/[\s\n\t]+/g, '')
}

/**
 * 检查联系方式的类型
 * @param phoneNumber 需要检测的联系方式
 * @returns {number} 联系方式的类型
 */
export function checkPhoneType(phoneNumber: string): number {
  if (phoneNumber === null || typeof phoneNumber === 'undefined' || phoneNumber === '') {
    return 0
  } else if (/^[+86]?1[34578]\d{9}$/.test(phoneNumber)) {
    return 1
  } else if (/^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}([-]\d{2,4})?$/.test(phoneNumber)) {
    return 2
  }
  return -1
}

/**
 * 判断用户是否使用PC端打开
 * @return {boolean}
 */
export function browserType(): boolean {
  let sUserAgent = navigator.userAgent.toLowerCase()
  let bIsIpad = sUserAgent.indexOf('ipad') > 0
  let bIsIphoneOs = sUserAgent.indexOf('iphone os') > 0
  let bIsMidp = sUserAgent.indexOf('midp') > 0
  let bIsUc7 = sUserAgent.indexOf('rv:1.2.3.4') > 0
  let bIsUc = sUserAgent.indexOf('ucweb') > 0
  let bIsAndroid = sUserAgent.indexOf('android') > 0
  let bIsCE = sUserAgent.indexOf('windows ce') > 0
  let bIsWM = sUserAgent.indexOf('windows mobile') > 0
  return bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM
}

/**
 * 生成十六进制颜色数组
 * @param length
 * @return {Array}
 */
//eslint-disable-next-line no-magic-numbers
export function generateHexadecimalColors(length = 10) {
  let colors = []
  for (let i = 0; i < length; i++) {
    //eslint-disable-next-line no-magic-numbers
    let color = '#' + ('00000' + ((Math.random() * 0x1000000) << 0).toString(16)).substr(-6)
    colors.push(color)
  }
  return colors
}

/**
 * 单位处理
 * @param communities
 * @param communityId
 * @return {{newCommunities: [], subdistricts: [], subdistrictId: number}}
 */
/*export function companyHandler(communities, communityId) {
  let companies = {
    subdistrictId: 0,
    subdistricts: [],
    newCommunities: []
  }
  if (communities !== null && communities.length > 0) {
    companies.subdistricts.push(communities[0].subdistrict)
    communities.forEach(item => {
      if (item.id === communityId) {
        companies.subdistrictId = item.subdistrictId
      }
      if (companies.subdistrictId === item.subdistrictId) {
        companies.newCommunities.push({
          id: item.id,
          name: item.name
        })
      }
      companies.subdistricts.forEach(subdistrict => {
        if (subdistrict.id !== item.subdistrict.id) {
          companies.subdistricts.push(item.subdistrict)
        }
      })
    })
  }
  return companies
}*/

/**
 * 包装AJAX
 * @param options
 * @param then
 * @param catches
 * @param csrfTokenCallback
 */

/*
export function $ajax(options, then, catches = null, csrfTokenCallback = null) {
  if (catches === null) {
    catches = (xhr, status, error) => {
      let responseJSON = xhr.responseJSON
      this.$message.error(responseJSON.messageError.defaultMessage)
    }
  }
  $.ajax(options)
    .then((result, status, xhr) => then && then(result, status, xhr))
    .catch((xhr, status, error) => catches && catches(xhr, status, error))
    .always((xhr, status, error) => {
      if (typeof options.method !== 'undefined' && options.method !== 'get') {
        $.ajax({
          url: getCsrfUrl,
          data: {
            data: Base64.encodeURI(
              JSON.stringify({
                host: location.hostname,
                timeStamp: new Date().getTime()
              })
            )
          }
        }).then(data => {
          if (data.state) {
            $("meta[name='X-CSRF-TOKEN']").attr('content', data.csrf)
            csrfTokenCallback && csrfTokenCallback(data.csrf)
          }
        })
      }
    })
}*/

/**
 * 数字格式化
 * @param number
 * @returns {string|*}
 */
export function formatNumber(number: string) {
  if (number.length <= 3) {
    return number
  }
  if (!/^([+-])?(\d+)(\.\d+)?$/.test(number)) {
    return number
  }
  let a = RegExp.$1,
    b = RegExp.$2,
    c = RegExp.$3,
    re = new RegExp(/(\\d)(\\d{3})(,|$)/)
  while (re.test(b)) {
    b = b.replace(re, '$1,$2$3')
  }
  return a + '' + b + '' + c
}
