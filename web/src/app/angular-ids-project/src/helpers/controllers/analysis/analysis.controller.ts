// interfaces
import { IDynamicChart } from '../../../components/dynamicChart/dynamicChart.interface';
import { IDatePickers } from '../../interfaces/datePicker.interface';

/**
 * base class for Analysis pages
 */
export class AnalysisController {

  protected $scope: any;

  constructor($scope: any, private datePickers: IDatePickers, private defaultChartType: string, private prefix: string) {

    $scope.yaxis = {
      range: 'adjusting',
      scaling: 'units',
      lower: null,
      upper: null
    };

    $scope.prefix = prefix;
    $scope.export = null;
    $scope.format = datePickers.format;
    $scope.datePickerRegex = datePickers.regex;

    $scope.selectedAccounts = [];
    $scope.selectedChartType = defaultChartType;
    $scope.chartData = this.resetChartData();

  }

  protected resetChartData(): IDynamicChart {
    let chartData: IDynamicChart = {
      datacolumns: Array<any>(),
      datax: {id: 'x'},
      datapoints: Array<any>()
    };

    return angular.copy(chartData);
  }

}
