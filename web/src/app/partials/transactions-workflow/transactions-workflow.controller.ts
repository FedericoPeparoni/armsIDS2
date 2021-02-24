/**
 * Required Interfaces
 */
import { ITransactionWorkflow, ITransactionWorkflowSpring, ITransactionWorkflowScope } from './transactions-workflow.interface';
import { ICurrencySpring } from '../currency-management/currency-management.interface';
import { IRoleData } from '../roles/roles.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

/**
 * Required Services
 */
import { TransactionsWorkflowService } from './service/transactions-workflow.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { RolesService } from '../roles/service/roles.service';

/**
 * Required Controllers
 */
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

/**
 * Controls all CRUD operations for creating a Transaction Approval Workflow
 */
export class TransactionsWorkflowController extends CRUDFormControllerUserService {

  /** @ngInject */
  constructor(
    protected $scope: ITransactionWorkflowScope, private transactionsWorkflowService: TransactionsWorkflowService,
    private currencyManagementService: CurrencyManagementService, private rolesService: RolesService,
    private $translate: angular.translate.ITranslateService
  ) {
    super($scope, transactionsWorkflowService);
    super.setup();

    $scope.addNewLevel = () => this.addNewLevel();
    $scope.refreshAll = () => this.refreshAll();
    $scope.createWorkflowItems = () => this.createWorkflowItems();
    $scope.updateWorkflowItems = () => this.updateWorkflowItems();
    $scope.removeExistingLevel = () => this.removeExistingLevel();
    $scope.createNewWorkflow = (steps: number) => this.createNewWorkflow(steps);
    $scope.updateItemApproval = (fld: string, level: number, idx: number) => this.updateItemApproval(fld, level, idx);
    $scope.updateItemData = (data: any, fld: string, id: number, idx: number) => this.updateItemData(data, fld, id, idx);
    $scope.updateApprovalDocumentLevel = (twf: ITransactionWorkflow) => this.updateApprovalDocumentLevel(twf);

    $scope.list = [];
    $scope.levelLabel = this.$translate.instant('Level');

    this.getCurrencies();
    this.getStatusTypes();
    this.getRoles();
    this.getFilterParameters();
  }

  /**
   * Update flag for 'approval_document_required'
   */
  private refreshAll(): void {
    this.transactionsWorkflowService.getAllWorkflowLevels().then((resp: ITransactionWorkflowSpring) => {
      this.$scope.list = resp.content;
      this.checkExistingWorkflow();
    });
  }

  /**
   * Update flag for 'approval_document_required'
   */
  private updateApprovalDocumentLevel(twf: ITransactionWorkflow): void {
    const level = twf ? twf.level : -1;

    this.$scope.list.forEach((item: ITransactionWorkflow) => {
      if (item.approval_document_required) {
        this.$scope.approval_document_level = item;
      }

      item.approval_document_required = (item.level === level ? true : false);
    });
  }

  /**
   * Get all available currencies from the API
   */
  private getCurrencies(): void {
    this.$scope.fullCurrencyList = [];

    this.currencyManagementService.listAll().then((data: ICurrencySpring) => {
      this.$scope.fullCurrencyList = data.content;
    });
  }

  /**
   * Get all status types from the service
   */
  private getStatusTypes(): void {
    this.$scope.statusTypes = this.transactionsWorkflowService.getStatusTypes();
  }

  /**
   * Get all roles/groups from the API
   * @following: checkExistingWorkflow
   */
  private getRoles(): void {
    this.rolesService.listAll().then((data: IRoleData) => {
      this.$scope.roles = data.content;

      this.checkExistingWorkflow();
    });
  }

  /**
   * Determine if workflow steps exist and set the flag accordingly.
   * @param {initalNumberOfSteps} number: preserves initial number of steps, controls how to save if steps are added or removed
   */
  private checkExistingWorkflow(): void {
    this.$scope.steps = this.$scope.list.length;

    if (this.$scope.list.length) {
      this.$scope.createNew = false;
      this.$scope.initalNumberOfSteps = this.$scope.steps;
      this.$scope.newSteps = this.$scope.steps;

      this.$scope.list.forEach((item: ITransactionWorkflow) => {
        if (item.approval_document_required) {
          this.$scope.approval_document_level = item;
        }
      });
    } else {
      this.$scope.createNew = true;
    };
  }

  /**
   * Creates a new workflow, deletes all existing steps
   * @param {number} steps - The number of steps to create
   */
  private createNewWorkflow(steps: number): void {
    this.$scope.list = [];

    this.$scope.initalNumberOfSteps = steps;

    this.$scope.createNew = true;

    for (let i = 0; i < steps; i++) {
      let item: ITransactionWorkflow = this.transactionsWorkflowService.getModel();

      item.level = i;
      item.approval_name = `${this.$scope.levelLabel} ${i}`;

      if (i === 0) {
        item.status_type = this.$scope.statusTypes[0];
        item.delete = true;
      } else if (i === (steps - 1)) {
        item.status_type = this.$scope.statusTypes[2];
      } else {
        item.status_type = this.$scope.statusTypes[1];
      };

      this.$scope.list.push(item);
    };
  }

  /**
   * Adds a new step to the end of the workflow
   */
  private addNewLevel(): void {
    let item: ITransactionWorkflow = this.transactionsWorkflowService.getModel();

    item.level = this.$scope.list.length;
    item.status_type = this.$scope.statusTypes[2];
    item.approval_name = `${this.$scope.levelLabel} ${item.level}`;

    this.$scope.list[this.$scope.list.length - 1].status_type = this.$scope.statusTypes[1];
    this.$scope.list.push(item);

    this.$scope.steps = this.$scope.list.length;
  }

  /**
   * Deletes an existing step from the end of the workflow
   */
  private removeExistingLevel(): void {
    this.$scope.list.pop();

    this.$scope.list[this.$scope.list.length - 1].status_type = this.$scope.statusTypes[2];

    this.$scope.steps = this.$scope.list.length;
  }

  /**
   * Persists changes made on form to objects in the $scope.list[] object
   * @param {any} data - The data object selected by the user on the UI
   * @param {string} fld - The field in $scope.list[] to update
   * @param {number} id - The link between the id of the data and the $scope.list[]
   * @param {number} idx - The item in $scope.list[] to update
   */
  private updateItemData(data: any, fld: string, id: number, idx: number): void {
    this.$scope.list[idx][fld] = data.find((obj: any): any => {
      if (obj.id === id) {
        return obj;
      };
    });
  }

  /**
   * Persists changes made on form using approval column dropdowns to objects in the $scope.list[] object
   * @param {string} fld - The field in $scope.list[] to update
   * @param {number} level - The level/step in $scope.list[]
   * @param {number} idx - The item in $scope.list[] to update
   */
  private updateItemApproval(fld: string, level: number, idx: number): void {
    this.$scope.list[idx][fld] = level;
  }

  /**
   * POST $scope.list[] to the API, delete all existing records
   */
  private createWorkflowItems(): void {
    if (!this.checkWorkflowItems()) {
      return;
    }

    this.$scope.error = null;

    this.transactionsWorkflowService.create(this.$scope.list).then((a: any) => {
      this.$scope.createNew = false;

      this.list();
    }, (error: any) => {
      this.$scope.error = {
        error: {
          data: error.data
        }
      };
    });
  }

  /**
   * Updates the existing workflow steps
   * PUT: Changes to existing steps;
   * POST: New levels that have been added
   * DELETE: Levels that have been removed
   */
  private updateWorkflowItems(): void {
    if (!this.checkWorkflowItems()) {
      return;
    }

    const list = this.$scope.list;

    for (let i = 0; i < list.length; i++) {
      if (i < this.$scope.initalNumberOfSteps) {
        this.transactionsWorkflowService.update(list[i], i);
      } else {
        this.transactionsWorkflowService.createNew(list[i], i);
      }
    }

    if (list.length < this.$scope.initalNumberOfSteps) {
      for (let j = list.length; j < this.$scope.initalNumberOfSteps; j++) {
        this.transactionsWorkflowService.delete(j);
      }
    }
    this.$scope.initalNumberOfSteps = list.length;
  }

  private checkWorkflowItems(): boolean {
    this.$scope.errorWorkflowItems = [];
    this.$scope.erroredLevel = [];
    this.$scope.list.forEach((element: any) => {
      const { status_type,
              level,
              approval_under_level,
              approval_over_level,
              rejected_level,
              threshold_amount,
              threshold_currency } = element;

      // 'Approval Under' & 'Approval Equal Or Over' should be always higher then the current level
      if (status_type !== 'FINAL' && (approval_under_level < level || approval_over_level < level)) {
        this.addErrorWorkflowItems(element, 'Approval Level should be higher then the current level');
      }
      // 'Threshold Amount' and 'Threshold Currency' should be specified if 'Approval Under' & 'Approval Equal Or Over' are not equal
      if (approval_under_level !== approval_over_level && (!threshold_amount || !threshold_currency)) {
        this.addErrorWorkflowItems(element, 'Please enter Threshold Amount and Threshold Currency');
      }
      // 'Rejected Level' should be always lower then the current level
      if (rejected_level > level) {
        this.addErrorWorkflowItems(element, 'Rejected Level should be lower then the current level');
      }
    });

    if (this.$scope.errorWorkflowItems.length) {
      this.$scope.error = this.showWorkflowItemsWarning(this.$scope.errorWorkflowItems);
      return false;
    }
    return true;
  }

  private showWorkflowItemsWarning(error: Array<Object>): IExtendableError {
    const workflowItemsWarning: any = {
      data: {
        error: 'Please check following requirements:',
        field_errors: error
      }
    };
    return { error: workflowItemsWarning };
  }

  private erroredLevel(level: number): void {
    if (this.$scope.errorWorkflowItems && !this.$scope.erroredLevel.includes(level)) {
      this.$scope.erroredLevel.push(level);
    }
  }

  private addErrorWorkflowItems(element: any, message: string): void {
    this.$scope.errorWorkflowItems.push({ level: element.level, field: element.approval_name, message: message });
    this.erroredLevel(element.level);
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }
}
