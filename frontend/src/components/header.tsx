import Head from 'next/head'

export default function Header({ children }: any) {
  return (
    <>
      <Head>
        <meta charSet="utf-8" />
        <meta name="renderer" content="webkit" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
        <title>{children} - 社区居民联系方式管理系统</title>
        <meta name="keywords" content="街道, 社区, 居民, 联系方式, 管理系统" />
        <meta name="description" content="这是为社区工作者开发的一款居民联系方式管理系统，使用Java编程语言进行开发，保证安全。" />
        <meta httpEquiv="content-language" content="zh-CN" />
        <meta name="author" content="廿二月的天" />
        <link rel="icon" href="/images/favicon.ico" />
      </Head>
    </>
  )
}
