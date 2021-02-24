export class DebounceService {

  /** @ngInject */
  constructor(private $q: ng.IQService, private $timeout: ng.ITimeoutService) { }

  /**
   * @param  {Function} func - The function to debounce
   * @param  {number} wait - The number of miliseconds to wait
   * @param  {boolean} immediate - Optional conditional which, if true, will immediately resolve the function
   */
  public debounce(func: Function, wait: number, immediate: boolean = false): () => ng.IPromise<{}> {
    let deferred = this.$q.defer();
    let timeout = null;
    let self = this;

    // use a deferred object that will be resolved
    // when we need to actually call the function
    return function(): ng.IPromise<{}> {
      let args = arguments;
      let later = (): void => {

        if (!immediate) {
          deferred.resolve(func.apply(self, args));
          deferred = self.$q.defer();
        }
      };

      if (timeout) {
        self.$timeout.cancel(timeout);
      }
      timeout = self.$timeout(later, wait);
      if (immediate) {
        self.$timeout.cancel(timeout);
        deferred.resolve(func.apply(self, args));
        deferred = self.$q.defer();
      }

      return deferred.promise;
    };
  }
}
