import { IReleaseNote } from './release-notes.interface';

export let endpoint: string = 'releasenotes';

export class ReleaseNoteService {

    /** @ngInject */
    constructor(protected Restangular: restangular.IService) {
    }

    public getReleaseNotes(): restangular.ICollectionPromise<IReleaseNote> {
        return this.Restangular.all(`${endpoint}`).getList();
    }
}
