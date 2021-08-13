import '@scss'
import 'bootstrap'
import '@coreui/coreui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

$(window).on('load', () => NProgress.done())
$(document).ready(() => NProgress.start())
