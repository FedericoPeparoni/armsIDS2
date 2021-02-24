// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface ICachedEvent {
    id: number;
    target: string;
    method_name: string;
    param_types: Array<string>;
    exceptions: Array<string>;
    caches: Array<string>;
    results: Array<ICachedEventResult>;
    metadata: Array<ICachedEventMetadata>;
    retry_count: number;
    last_attempt: Date;
}

export interface ICachedEventMessage {
    title?: string;
    description?: string;
    thrown?: boolean;
}

export interface ICachedEventResult {
    clazz: string;
    result: string;
    created_at: string;
    created_by: string;
}

export interface ICachedEventMetadata {
    type: string;
    action: string;
    resource: string;
    statement: string;
}

export interface ICachedEventScope extends ng.IScope {
    editable: ICachedEvent;
    error: { error: IRestangularResponse};
    highlightSearch: string;
    list: Array<ICachedEvent>;
    message: ICachedEventMessage;
    nextRetryCycle: Date;
    pagination: ISpringPageableParams;
    retryInProgress: boolean;
    search: string;
    commaSeperate(array: Array<any>, property: string): string;
    delete(id: number): ng.IPromise<void>;
    refresh(): ng.IPromise<void>;
    retry(id: number): ng.IPromise<void>;
}
