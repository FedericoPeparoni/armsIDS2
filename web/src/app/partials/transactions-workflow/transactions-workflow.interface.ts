// interfaces
import { ICurrency } from "../currency-management/currency-management.interface";
import { IRole } from "../roles/roles.interface";
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface ITransactionWorkflow {
  id?: number;
  level: number;
  delete: boolean;
  approval_name: string;
  approval_group: IRole;
  status_type: string;
  threshold_amount: number;
  threshold_currency: number;
  approval_under_level: number;
  approval_over_level: number;
  rejected_level: number;
  version: number;
  approval_document_required?: boolean;
}

export interface ITransactionWorkflowSpring extends ISpringPageableParams {
  content: Array<ITransactionWorkflow>;
}

export interface ITransactionWorkflowScope {
  fullCurrencyList: Array<ICurrency>;
  roles: Array<IRole>;
  statusTypes: Array<string>;
  getStatusTypes: Function;
  createWorkflowItems: Function;
  updateItemData: Function;
  updateItemApproval: Function;
  createNewWorkflow: Function;
  updateWorkflowItems: Function;
  addNewLevel: Function;
  removeExistingLevel: Function;
  list: Array<ITransactionWorkflow>;
  steps: number;
  initalNumberOfSteps: number;
  createNew: boolean;
  error: IExtendableError;
  levelLabel: string;
  updateApprovalDocumentLevel: Function;
  refreshAll: Function;
  approval_document_level: ITransactionWorkflow;
  newSteps: number;
  errorWorkflowItems: any;
  erroredLevel: any;
  filterParameters: object;
  pagination: ISpringPageableParams;
}
