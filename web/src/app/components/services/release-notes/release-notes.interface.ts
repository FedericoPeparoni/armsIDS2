export interface IReleaseNote {
    id: number,
    title: string;
    number: number;
    reopened: boolean;
    release_category: IReleaseCategory;
    release_version: string;
}

export interface IReleaseCategory {
    id: number;
    title: string;
}
