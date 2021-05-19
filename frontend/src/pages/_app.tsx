import type { AppProps } from 'next/app'
import '@/src/scss'

export default function App({ Component, pageProps }: AppProps) {
  return <Component {...pageProps} />
}
