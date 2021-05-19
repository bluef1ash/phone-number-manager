export const base: string = 'http://127.0.0.1:8080'
export const userBase: string = base + '/user'
export const accountBase: string = base + '/account'
export const commentBase: string = base + '/comment'
export const directMessageBase: string = base + '/directMessage'
export const orderPayBase: string = base + '/orderPay'
export const postsBase: string = base + '/posts'
export const shopBase: string = base + '/shop'

// 子地址
export const account = {
  //用户登录
  login: accountBase + '/login',
  //用户登出
  logout: accountBase + '/logout',
  //获取图形验证码
  getRecaptcha: accountBase + '/getRecaptcha',
  //图形验证码检查
  imageCodeCheck: accountBase + '/imageCodeCheck',
  //发送短信或者邮箱验证码
  sendCode: accountBase + '/sendCode',
  //找回密码验证
  forgotPassword: accountBase + '/forgotPassword',
  //重设密码
  restPassword: accountBase + '/restPassword',
  //用户注册
  register: accountBase + '/register',
  //社交登录
  socialLogin: accountBase + '/socialLogin',
  //社交登录，检查邀请码
  invitationRegister: accountBase + '/invitationRegister',
  //解除绑定社交账户
  socialUnBuild: accountBase + '/socialUnBuild',
  //保存昵称
  saveNickName: accountBase + '/saveNickName',
  //保存性别
  saveSex: accountBase + '/saveSex',
  //保存网址
  saveUrl: accountBase + '/saveUrl',
  //保存个人描述
  saveDescription: accountBase + '/saveDescription',
  //获取收货地址
  getAddresses: accountBase + '/getAddresses',
  //保存默认收货地址
  saveDefaultAddress: accountBase + '/saveDefaultAddress',
  //保存收货地址
  saveAddress: accountBase + '/saveAddress',
  //删除收货地址
  deleteAddress: accountBase + '/deleteAddress',
  //保存用户名
  saveUsername: accountBase + '/saveUsername',
  //后台修改地址
  editPassword: accountBase + '/editPassword',
  //获取公众号二维码
  getLoginQrCode: accountBase + '/getLoginQrCode',
  //关注并登录
  mpLogin: accountBase + '/mpLogin',
  //关注并使用邀请码登录
  mpLoginInvitation: accountBase + '/mpLoginInvitation'
}
export const comment = {
  //获取评论
  get: commentBase + '/get',
  //获取tips
  getTips: commentBase + '/getTips',
  //给评论赞踩
  vote: commentBase + '/vote',
  //获取某一组评论的踩赞数据
  voteData: commentBase + '/voteData',
  //置顶评论
  sticky: commentBase + '/sticky',
  //发布评论
  publish: commentBase + '/publish',
  //获取小工具里面的最新评论
  getNew: commentBase + '/getNew'
}
export const directMessage = {
  //给用户发私信
  send: directMessageBase + '/send',
  //获取私信列表
  get: directMessageBase + '/get',
  //获取私信对话
  getDialogue: directMessageBase + '/getDialogue',
  //获取新的私信数量
  getNew: directMessageBase + '/getNew'
}
export const orderPay = {
  //获取用户的订单
  get: orderPayBase + '/get',
  //卡密充值
  cardPay: orderPayBase + '/cardPay',
  //检查支付方式
  checkPayType: orderPayBase + '/checkPayType',
  //批量支付
  BatchPayment: orderPayBase + '/BatchPayment',
  //获取允许的支付
  allowPayType: orderPayBase + '/allowPayType',
  //开始支付
  buildOrder: orderPayBase + '/buildOrder',
  //支付确认
  payCheck: orderPayBase + '/payCheck'
}
export const other = {}
export const posts = {
  //获取文章模块内容（分页显示）
  getList: postsBase + '/getList',
  //获取公告列表
  getAnnouncements: postsBase + '/getAnnouncements',
  //获取最新公告
  getLatestAnnouncement: postsBase + '/getLatestAnnouncement',
  //获取视频播放列表
  getVideos: postsBase + '/getVideos',
  //获取语音播放字符串
  getAudio: postsBase + '/getAudio',
  //获取外链视频的html
  getVideoHtml: postsBase + '/getVideoHtml',
  //获取隐藏段代码内容
  getHiddenContent: postsBase + '/getHiddenContent',
  //获取文章封面图
  getPoster: postsBase + '/getPoster',
  //获取文章相关信息
  geData: postsBase + '/geData',
  //文章顶踩
  vote: postsBase + '/vote',
  //获取文章下载数据
  getDownloadData: postsBase + '/getDownloadData',
  //获取下载跳转页面数据
  getDownloadPageData: postsBase + '/getDownloadPageData',
  //获取下载文件的真实地址
  downloadFile: postsBase + '/downloadFile',
  //投稿
  publish: postsBase + '/publish',
  //检查文章编辑权限
  checkWriteUser: postsBase + '/checkWriteUser'
}
export const shop = {
  //通过ID获取商品信息
  getItemsData: shopBase + '/getItemsData',
  //领取优惠劵
  couponReceive: shopBase + '/couponReceive',
  //获取我的优惠劵
  getMyCoupons: shopBase + '/getMyCoupons',
  //删除我的优惠劵
  deleteMyCoupon: shopBase + '/deleteMyCoupon',
  //获取商品优惠劵信息
  getCouponsByPostId: shopBase + '/getCouponsByPostId',
  //积分抽奖
  lottery: shopBase + '/lottery',
  //获取购买结果信息
  getUserBuyInfo: shopBase + '/getUserBuyInfo',
  //快递查询
  getOrderExpress: shopBase + '/getOrderExpress',
  //图片上传
  imageUpload: shopBase + '/imageUpload'
}
export const user = {
  //获取当前登录用户的个人信息
  getInfo: userBase + '/getInfo',
  //获取用户页面的用户信息
  getAuthorInfo: userBase + '/getAuthorInfo',
  //保存用户cover
  saveCover: userBase + '/saveCover',
  //保存avatar
  saveAvatar: userBase + '/saveAvatar',
  //获取用户的评论列表
  getAuthorComments: userBase + '/getAuthorComments',
  //获取用户的关注列表
  getAuthorFollowing: userBase + '/getAuthorFollowing',
  //获取用户的粉丝列表
  getAuthorFollowers: userBase + '/getAuthorFollowers',
  //检查多个ID是否关注
  checkFollowByIds: userBase + '/checkFollowByIds',
  //关注与取消关注
  authorFollow: userBase + '/authorFollow',
  //检查用户是否关注某人，或者是不是本人
  checkFollowing: userBase + '/checkFollowing',
  //用户收藏与取消收藏
  setFavorites: userBase + '/setFavorites',
  //用户收藏列表
  getFavorites: userBase + '/getFavorites',
  //获取用户设置项
  getAuthorSettings: userBase + '/getAuthorSettings',
  //用户头像选择
  changeAvatar: userBase + '/changeAvatar',
  //储存用户的QRcode
  saveQrCode: userBase + '/saveQrCode',
  //获取用户的邀请码列表
  geInvitation: userBase + '/geInvitation',
  //获取用户的公开信息
  getPublicData: userBase + '/getPublicData',
  //搜索用户
  search: userBase + '/search',
  //小工具用户面板
  geWidget: userBase + '/geWidget',
  //用户签到
  mission: userBase + '/mission',
  //获取签到数据
  getMission: userBase + '/getMission',
  //获取签到数据
  getMissionList: userBase + '/getMissionList',
  //随机获取认证用户
  getVerifyUsers: userBase + '/getVerifyUsers',
  //获取公众号关注二维码
  getMpQrCode: userBase + '/getMpQrCode',
  //检查用户是否已经关注公众号
  checkSubscribe: userBase + '/checkSubscribe',
  //提交认证信息
  submitVerify: userBase + '/submitVerify',
  //提交认证信息
  getCurrentUserAttachments: userBase + '/getCurrentUserAttachments',
  //获取用户任务数据
  getTaskData: userBase + '/getTaskData',
  //获取财富页面信息
  getUserGoldData: userBase + '/getUserGoldData',
  //获取财富页面积分、余额记录
  getGold: userBase + '/getGold',
  //获取财富排行信息
  getGoldTop: userBase + '/getGoldTop',
  //获取vip信息
  getVipInfo: userBase + '/getVipInfo',
  //获取当前用户的邮箱
  getEmail: userBase + '/getEmail',
  //获取用户的权限
  getUserRole: userBase + '/getUserRole',
  //小工具用户面板
  getWidget: userBase + '/geWidget'
}
