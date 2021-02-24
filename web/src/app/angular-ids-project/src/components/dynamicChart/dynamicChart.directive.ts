import { IYAxisRange } from './yaxis.interface';

interface IDynamicChartChart {
  datapoints: Array<any>;
}

interface IDynamicChartScope {
  yaxis: IYAxisRange;
  chart: IDynamicChartChart;
  yLabel: string;
}

interface IDynamicChartDirectiveScope extends ng.IScope {
  DynamicChart: IDynamicChartScope;
  chart: string;
  labelFormat: Function;
  tooltipNameFormat: Function;
  tooltipTitleFormat: Function;
  valueFormat: Function;
  datapoints: Object;
}

/** @ngInject */
export function dynamicChart($timeout: angular.ITimeoutService): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/dynamicChart/dynamicChart.html',
    controller: class DynamicChartController{},
    controllerAs: 'DynamicChart',
    bindToController: true,
    replace: true,
    scope: {
      chart: '=',
      yaxis: '=',
      yLabel: '='
    },
    transclude: true,
    link: function(scope: IDynamicChartDirectiveScope, elem: any, attrs: any): void {

      scope.labelFormat = (v: string/*, id: number, i: number, j: number*/) => { return v; };

      scope.tooltipNameFormat = (n: string/*, r: number, id: number, index: number*/) => { return n; };

      scope.tooltipTitleFormat = (x: string) => { return x; };

      scope.valueFormat = (v: number/*, r: number, i: number, index: number*/) => { return (v * getScaling()).toFixed(2); };

      function getScaling(): number {
        let scaling: number = 0;

        switch (scope.DynamicChart.yaxis.scaling) {
          case 'thousands':
            scaling = 1000;
            break;
          case 'millions':
            scaling = 1000000;
            break;
          default: scaling = 1; // does not alter data
        }

        return scaling;
      }

      // returns the datapoints scaled properly for the chart
      function returnScaledPoints(datapoints: Array<IDynamicChartChart>): Array<IDynamicChartChart> {
        for (let point in datapoints) {
          if (datapoints.hasOwnProperty(point)) {
            for (let data in datapoints[point]) {
              if (data !== 'x' && datapoints[point].hasOwnProperty(data)) {
                datapoints[point][data] = datapoints[point][data] / getScaling();
              }
            }
          }
        }

        return datapoints;
      }

      scope.$watch('DynamicChart.chart', () => {

        if (scope.DynamicChart.chart.datapoints.length === 0) {
          return; // this prevents a rendering with no data, saves it from not getting the width correctly
        }

        // we create aa datapoints array of objects, reason is because of the two-way binding. careful with changing values
        let datapoints = [];

        for (var i = 0; i < scope.DynamicChart.chart.datapoints.length; i++) {

          if (typeof datapoints[i] === 'undefined') {
            datapoints[i] = {};
          }

          for (var j in scope.DynamicChart.chart.datapoints[i]) {
            if (scope.DynamicChart.chart.datapoints[i].hasOwnProperty(j)) {
              datapoints[i][j] = angular.copy(scope.DynamicChart.chart.datapoints[i][j]);
            }
          }
        }

        // because the yaxis is two-way binding, we assign custom variables and build that chart with that
        let upper: number = angular.copy(scope.DynamicChart.yaxis.upper);
        let lower: number = angular.copy(scope.DynamicChart.yaxis.lower);

        upper = upper !== null ? (upper / getScaling()) : null;
        lower = lower !== null ? (lower / getScaling()) : null;

        datapoints = returnScaledPoints(datapoints); // bind the scope variable

        scope.chart = '';

        // this is for analysis only, so if this directive is to be used for other things, this may have to be moved
        for (let key = 0; key < datapoints.length; key++) {
          if (datapoints[key].x.match(/^20\d{2}\-\d{2}\-\d{2}/)) {
            datapoints[key].x = datapoints[key].x.substr(0, 10); // removes T00:00:000Z from timestamps
          }
        }

        scope.datapoints = datapoints;

        $timeout(() => {
          scope.chart = `<c3chart bindto-id="chart-timeseries" label-format-function="labelFormat"  chart-data="datapoints" chart-columns="DynamicChart.chart.datacolumns"
      chart-x="DynamicChart.chart.datax">
        <!--<chart-tooltip title-format-function="tooltipTitleFormat" />-->
        <chart-tooltip label-format-function="labelFormat" />
        <chart-tooltip value-format-function="valueFormat" />
        <!--<chart-tooltip name-format-function="tooltipNameFormat" />-->
        <chart-size chart-height="600" />
        <chart-axis>
        <chart-axis-x axis-id="x" axis-type="category" axis-x-format="%Y-%m-%d" />
        <chart-axis-y axis-id="y"
      axis-position="middle-right"
      axis-label="${scope.DynamicChart.yLabel}"
      tick-rotate="2"
      axis-min="${lower}"
      axis-max="${upper}"/>
        </chart-axis>
        </c3chart>`;
        }, 0);

      }, true);
    }
  };
}
