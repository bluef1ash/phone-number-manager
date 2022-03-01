import type { IRoute } from 'umi';

export default function access(initialState: { components: string[] }) {
  const { components } = initialState || {};
  return {
    normalRouteFilter({ name }: IRoute) {
      return (
        typeof components === 'undefined' || components.length === 0 || components.includes(name)
      );
    },
    importData(dataType: string) {
      return (
        typeof components === 'undefined' ||
        components.length === 0 ||
        components.includes(dataType)
      );
    },
  };
}
