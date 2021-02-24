export interface ICancellablePromise {
  cancel: () => void,
  pending: () => boolean,
  promise: Promise<any>
}

export interface ICancellablePromiseException {
  error?: any,
  isCancelled: boolean,
  response?: any
}
