import { Button, message, notification } from 'antd';
import { useIntl } from 'umi';
import defaultSettings from '../config/defaultSettings';

const { pwa } = defaultSettings;
const isHttps = document.location.protocol === 'https:';

if (pwa) {
  window.addEventListener('sw.offline', () => {
    message.warning(useIntl().formatMessage({ id: 'app.pwa.offline' })).then();
  });
  window.addEventListener('sw.updated', (event: Event) => {
    const e = event as CustomEvent;
    const reloadSW = async () => {
      const worker = e.detail && e.detail.waiting;
      if (!worker) {
        return true;
      }
      await new Promise((resolve, reject) => {
        const channel = new MessageChannel();
        channel.port1.onmessage = (msgEvent) => {
          if (msgEvent.data.error) {
            reject(msgEvent.data.error);
          } else {
            resolve(msgEvent.data);
          }
        };
        worker.postMessage({ type: 'skip-waiting' }, [channel.port2]);
      });
      window.location.reload();
      return true;
    };
    const key = `open${Date.now()}`;
    const btn = (
      <Button
        type="primary"
        onClick={() => {
          notification.close(key);
          reloadSW().then();
        }}
      >
        {useIntl().formatMessage({ id: 'app.pwa.serviceworker.updated.ok' })}
      </Button>
    );
    notification.open({
      message: useIntl().formatMessage({ id: 'app.pwa.serviceworker.updated' }),
      description: useIntl().formatMessage({ id: 'app.pwa.serviceworker.updated.hint' }),
      btn,
      key,
      onClose: async () => null,
    });
  });
} else if ('serviceWorker' in navigator && isHttps) {
  const { serviceWorker } = navigator;
  if (serviceWorker.getRegistrations) {
    serviceWorker.getRegistrations().then((sws) => {
      sws.forEach((sw) => {
        sw.unregister().then();
      });
    });
  }
  serviceWorker.getRegistration().then((sw) => {
    if (sw) sw.unregister().then();
  });
  if (window.caches && (await window.caches.keys())) {
    caches.keys().then((keys) => {
      keys.forEach((key) => {
        caches.delete(key).then();
      });
    });
  }
}
