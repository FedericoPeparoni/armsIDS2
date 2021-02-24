// sidebar scope
export interface ISideBarScope extends ng.IScope {
  toggle: boolean;
  highlight: Function;
}

// sidebar link scope
export interface ISideBarLinkScope extends ng.IScope {
  active: boolean;
  attrs: {
    uiSref: string;
  };
}

export interface ISideBarCategory {
  title: string;
  icon: string;
  permissions: Array<string>;
  links: Array<ISidebarLink>;
}

export interface ISidebarLink extends ISideBarCategory {
  category: string;
  url: string;
}
