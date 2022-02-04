export default [
  {
    name: '登录',
    path: '/account/login',
    component: './account/Login',
    layout: false,
    menuRender: false,
    headerRender: false,
    menuHeaderRender: false,
  },
  {
    path: '/system/user',
    name: './user',
    component: './user',
    access: 'normalRouteFilter',
  },
  {
    path: '/system/permission',
    name: './permission',
    component: './permission',
    access: 'normalRouteFilter',
  },
  {
    path: '/system/configuration',
    name: './configuration',
    component: './configuration',
    access: 'normalRouteFilter',
  },
  { path: '/welcome', name: '/welcome', component: './welcome' },
  { path: '/', redirect: '/welcome' },
  { component: './404' },
];
