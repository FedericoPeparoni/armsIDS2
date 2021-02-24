// interface
import { IAboutScope } from './about.interface';

// service
import { AboutService } from './service/about.service';
import { ReleaseNoteService } from '../../components/services/release-notes/release-notes.service';
import { IReleaseNote } from '../../components/services/release-notes/release-notes.interface';

export class AboutController {

  /** 
   * Maximum digits for individual semantic versioning parts.
   */
  private SEMVER_PART_MAX_DIGITS: number = 3;

  /* @ngInject */
  constructor(private $scope: IAboutScope, private aboutService: AboutService, private releaseNoteService: ReleaseNoteService) {

    // allow multiple open at once
    $scope.oneAtATime = false;

    $scope.list = aboutService.list();
    $scope.orderByReleaseVersion = (releaseNote: IReleaseNote) => this.orderByReleaseVersion(releaseNote);

    // load release notes into scope
    releaseNoteService.getReleaseNotes().then((data: IReleaseNote[]) => $scope.releasenotes = data);
  }

  /**
   * Parse release note version and return negative number value.
   *
   * @param releaseNote release note to parse
   */
  private orderByReleaseVersion(releaseNote: IReleaseNote): number {

    // split release note version into individual parts to format
    // for example, '1.2.34' would be formatted as '[001,002,034]'
    const version: Array<string> = releaseNote.release_version
      .split('.').map((value: string): string => {

      // remove any none digit characters
      const number: string = value.replace(/\D/g, '');

      // force three digit value by prepending '0' until in the format '###'
      const format: Array<string> = [];
      for (let i = number.length; i < this.SEMVER_PART_MAX_DIGITS; i++) {
        format.push('0');
      }
      format.push(number);

      // return joined formatted value
      return format.join('');
    });

    // return negavite (DESC) number representation of version
    return -Number(version.join(''));
  }
}
