// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { ISystemConfiguration } from '../system-configuration/system-configuration.interface';

export interface IPlugin {
    id: number;
    name: string;
    description: string;
    enabled: boolean;
    key: string;
    configurations: Array<ISystemConfiguration>;
    visible: boolean;
}

export interface IPluginScope extends angular.IScope {
    editable: IPlugin;
    error: { error: IRestangularResponse};
    highlightSearch: string;
    list: Array<IPlugin>;
    pagination: ISpringPageableParams;
    search: string;
    showHidden: boolean;
    edit(plugin: IPlugin): void;
    refresh(): angular.IPromise<void>;
    add(plugin: IPlugin, id: number): angular.IPromise<any>;
    update(plugin: IPlugin, id: number): angular.IPromise<any>;
    remove(plugin: IPlugin, id: number): angular.IPromise<any>;
}

export interface IPluginParams {
    hidden: string;
}
