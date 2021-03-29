import { Column } from '@ant-design/charts';
import type { ColumnConfig } from '@ant-design/plots/es/components/column';
import React from 'react';

export type ComputedChartColumnProps = {} & ColumnConfig & React.RefAttributes<unknown>;
const ComputedChartColumn: React.FC<ComputedChartColumnProps> = ({ ...restProps }) => (
  <Column
    style={{ margin: 20 }}
    label={{
      position: 'middle',
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    }}
    xAxis={{
      label: {
        autoHide: false,
        autoRotate: true,
      },
    }}
    yAxis={{
      label: {
        autoHide: false,
        autoRotate: true,
        formatter(text) {
          return `${text}人`;
        },
      },
    }}
    {...restProps}
  />
);
export default ComputedChartColumn;
