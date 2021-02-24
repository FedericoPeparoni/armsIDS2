import { IDynamicChart } from '../../../components/dynamicChart/dynamicChart.interface';
import { IYAxisRange } from '../../../components/dynamicChart/yaxis.interface';

export interface IAnalysisScope extends ng.IScope {
  format: string;
  yaxis: IYAxisRange;
  yLabel: Function;
  prefix: string;
  dataToDisplay: string;
  export: string;
  ajaxCall: boolean;
  showError: boolean;
  selectedChartType: string;
  selectedAccounts: Array<number>;
  datePickerRegex: string;
  chartData: IDynamicChart;
}
