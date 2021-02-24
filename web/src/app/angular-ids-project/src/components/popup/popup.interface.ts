export interface IPopupScope extends ng.IScope {
  text: string;
  createWarning: string;
  updateWarning: string;
  deleteWarning: string;
  popupTextInput: string;
  saveAs: boolean;
  close: () => void;
  confirm: () => void;
  setName: (text: string) => string;
}

export interface IPopupAttributes {
  popupLocalStorageEnable: string;
  popupEnabled: string;
  popupText: string;
  popupConfirm: string;
  $observe: any;
}
