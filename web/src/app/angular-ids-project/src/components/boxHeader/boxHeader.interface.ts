export interface IBoxHeaderScope extends ng.IScope {
    toggle: boolean;
    page: number;
    pageName: string;
    pageUrl: string;
    icon: string;
}

export interface IBoxHeaderUserGuide {
    content: Array<IBoxHeaderUserGuideSection>;
    date: Date,
    location: string;
    name: string;
    notes?: string;
    version: string;
}

export interface IBoxHeaderUserGuideSection {
    content?: Array<IBoxHeaderUserGuideSection>;
    identifier: string;
    name: string;
    page: number;
}
