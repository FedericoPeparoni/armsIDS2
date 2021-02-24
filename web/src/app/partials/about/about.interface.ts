import { IReleaseNote } from '../../components/services/release-notes/release-notes.interface';

export interface IAboutScope extends ng.IScope {
  list: Object;
  releasenotes: IReleaseNote[];
  isReleaseAccordionOpen: boolean[];
  orderByReleaseVersion(releaseNote: IReleaseNote): number;
}
