// removes the option that are lower then the current level as the level being considered

// interfaces
import {  ITransactionWorkflow } from '../../../../../partials/transactions-workflow/transactions-workflow.interface';


/** @ngInject */
export function hideLower(): (val: Array<ITransactionWorkflow>, idx: number) => Array<ITransactionWorkflow> {

  return (vals: Array<ITransactionWorkflow>, idx: number) => {
    const output = [];

    for (let val of vals) {
      if (idx < val.level) {
        output.push(val);
      }
    }

    return output;
  };

}
