export class DbqueryService {

  /**
   * This function creates an array of all
   * temporal groups, sorts them ascending
   *
   * These temporal groups are used along
   * the Y-Axis of the chart
   *
   * Temporal groups are one of
   * year/quarter/month/week
   *
   * Loops through the data and returns
   * an array of objects in the following format
   * {"x": "temp_group", "column1": 1, "column2": 1}
   *
   * @param data JSON data
   * @param yaxis comparison value
   * @param groupBY group by value
   * @returns {Array}
   */
  public getDataPoints(data: any[], yaxis: string, sortList: any): Object[] {
    const datapoints = [];
    const xAxisAttribute = sortList[0].value;
    const yAxisAttribute = sortList[1] ? sortList[1].value : null;

    const defaultKey = 'Amount';
    const defaultGroupName = 'Other';

    for (let item of data) {
      // return the item if already exist in datapoints

      // the bar is not displayed when a key have decimal point as a dot
      if (typeof(item[yAxisAttribute]) === 'number') {
        item[yAxisAttribute] = (item[yAxisAttribute]).toString().replace(/\./g, ',');
      }

      const itemXAxisAttribute = item[xAxisAttribute] ? item[xAxisAttribute] : defaultGroupName;
      const existingTarget = datapoints.find(((dp: any) => dp.x === itemXAxisAttribute));

      const target = existingTarget ? existingTarget : { x: itemXAxisAttribute };
      let groupName = item[yAxisAttribute] ? item[yAxisAttribute] : defaultGroupName;
      //calcolo il totale dei valori passati 
      let sum : number = 0;
      for(let i = 0; i < data.length; i++){ 
        sum = sum + data[i][yaxis];        
      }

      //controllo che la percentuale sia inferiore all'1%
      let percentuale : number = (item[yaxis] / sum) * 100;
      if(percentuale <  0.01){
        //se la percentuale è inferiore all'1% effettuo una somma dei valori raggruppandoli in "Other"
        groupName = "OTHER";
      }

      if (!yAxisAttribute) {
        target[defaultKey] = item[yaxis];
      } else {
        if(groupName === "OTHER"){
          target[groupName] = target[groupName] !== undefined ? target[groupName] + item[yaxis] : item[yaxis];
        }else{
          target[groupName] = item[yaxis];
        }
      }

      if (!existingTarget) {
        datapoints.push(target);
      }
    }
    // sort the buckets by date
    datapoints.sort((a: any, b: any) => {
      return a.x.localeCompare(b.x, undefined, {numeric: true, sensitivity: 'base'});
    });

    return datapoints;
  }

  /**
   * Builds an array of objects that contain
   * the names and types of columns in the
   * following format
   * {"id": "ColumnId", "type": "bar", "name": "Column Name"}
   * @param data        entire dataset from server
   * @param datapoints  datapoints from getDataPoints method
   * @param labelKey    takes the datapoint data and uses the labelKey to get data to use on the x-axis
   * @param type        chart type bar|line|pie etc.
   * @returns {Array}
   */
  public getColumnsLabels(data: any[], datapoints: Object[], labelKey: string, type: string): Object[] {
    let dataColumns = [];
    let ids = [];

    for (let point of datapoints) {
      for (let key in point) {
        if (key !== 'x') {
          ids.push(key);
        }
      }
    }

    for (let id of ids) {
      /**
       * id matches the id of the datapoints
       * type is the chart type   bar|line|pie etc.
       * name is the x label legend for this point
       */
      if (labelKey) {
        dataColumns.push({ 'id': id, 'type': type, 'name': id });
      } else {
        dataColumns.push({ 'id': id, 'type': type, 'name': null });
      }
    }

    return dataColumns;
  }

}
