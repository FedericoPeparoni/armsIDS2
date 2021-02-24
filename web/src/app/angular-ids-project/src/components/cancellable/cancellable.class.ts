// interfaces
import { ICancellablePromise, ICancellablePromiseException } from './cancellable.interface';
import { IPromise } from 'angular';

/**
 * Utility class for creating cancellable requests.
 */
export class Cancellable {

  /**
   * Wraps AngularJS Promise in a cancellable promise.
   *
   * @param promise promise to make cancellable
   */
  static promise(promise: IPromise<any>): ICancellablePromise {
    let hasCancelled = false;
    let isPending = true;

    const wrappedPromise = new Promise((resolve: any, reject: any) => {

      // handle promise fulfillment
      promise.then((val: any) => {
        isPending = false;
        hasCancelled
          ? reject(<ICancellablePromiseException>{isCancelled: true, response: val})
          : resolve(val);
      });

      // handle promise rejection
      promise.catch((err: any) => {
        isPending = false;
        hasCancelled
          ? reject(<ICancellablePromiseException>{isCancelled: true, error: err})
          : reject(<ICancellablePromiseException>{isCancelled: false, error: err});
      });
    });

    return <ICancellablePromise>{
      cancel: (): boolean => hasCancelled = true,
      pending: (): boolean => isPending,
      promise: wrappedPromise
    };
  }
}
