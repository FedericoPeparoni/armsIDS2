export let endpoint: string = 'languages';

export class LanguagesService {

  protected restangular: restangular.IService;

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    // to do -> anythinng?
  }

  public createLangFileByPart(part: string): ng.IPromise<any> {
    return this.Restangular.one(`${endpoint}/file/${part}`).get();
  }

}
