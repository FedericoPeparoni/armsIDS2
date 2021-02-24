// interfaces
import { IBoxHeaderScope, IBoxHeaderUserGuide, IBoxHeaderUserGuideSection } from './boxHeader.interface';

// constants
import { BoxHeaderConstant } from './boxHeader.constant';

/** @ngInject */
export function boxHeader(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/boxHeader/boxHeader.html',
    bindToController: false,
    scope: {
      icon: '=',
      page: '=?',
      pageName: '@?',
      toggle: '=?',
      title: '@',
      suppressDownload: '@',
      showClose: '@',
      closeFunc: '&'
    },
    link: (scope: IBoxHeaderScope, element: any, attrs: any): void => {
      scope.toggle = attrs.toggle ? false : null;
    },
    controller: BoxHeaderController,
    replace: true,
    transclude: true
  };
}

/** @ngInject */
export class BoxHeaderController {

  constructor(private $scope: IBoxHeaderScope, private $rootScope: ng.IRootScopeService, private $location: ng.ILocationService,
    private $translate: ng.translate.ITranslateService) {

    // espose user guide url from page number if page number exists
    // if no page in scope, parse from user guide using page name
    $scope.pageUrl = this.getUserGuideUrl($scope.page || this.findUserGuidePageNumber($scope.pageName));
    $rootScope.$on('$translateChangeSuccess', () =>
      $scope.pageUrl = this.getUserGuideUrl($scope.page || this.findUserGuidePageNumber($scope.pageName)));
  }

  /**
   * Return user guide location with page number only if page number is not null.
   * 
   * @param pageNum page number in user guide
   */
  private getUserGuideUrl(pageNum: number): string {

    // page number must be provided and defined
    if (pageNum === undefined || pageNum === null || pageNum <= 0) {
      return null;
    }

    // get base url from location path
    const baseUrl: string = this.$location.absUrl().split('#')[0];

    // set language code from translation service
    const langCode: string = this.$translate.use() || 'en';

    // build user guide url with provided page number reference
    return `${baseUrl}${BoxHeaderConstant.userGuide.location}${BoxHeaderConstant.userGuide.name}.${langCode}.pdf#page=${pageNum}`;
  }

  /**
   * Find user guide's page number from provided page name.
   * 
   * @param pageName page name to find in user guide
   */
  private findUserGuidePageNumber(pageName: string): number {

    // return null if page name is undefined as it is required
    if (pageName === undefined || pageName === null) {
      return null;
    }

    // parse section names from page name, return null if empty
    let sectionNames: Array<string> = this.parseSectionNames(pageName);
    if (sectionNames === undefined || sectionNames === null || sectionNames.length === 0) {
      return null;
    }

    // loop through each section name and find appropriate user content section in user guide
    // will loop until all section names have been parsed OR stop if section could not be found
    let level: number = 0;
    let section: IBoxHeaderUserGuide | IBoxHeaderUserGuideSection = BoxHeaderConstant.userGuide;
    while (sectionNames[level] && section !== null) {

      // find section name for current level
      let sectionName: string = sectionNames[level];

      // find section in content by section name
      section = section.content.find((section: IBoxHeaderUserGuideSection) => section.name === sectionName);

      // increase level for next loop
      level++;
    }

    // return page number from user guide section if found
    return section !== undefined && section !== null && 'page' in section
      ? (section as IBoxHeaderUserGuideSection).page : null;
  }

  /**
   * Parse user guide page name to get list of sections, must be separated by dot ('.').
   * 
   * @param pageName page name to parse sections from
   */
  private parseSectionNames(pageName: string): Array<string> {

    // ensure page name is defiend
    if (pageName === undefined || pageName === null) {
      return [];
    }

    // remove all whitespace and any leading or trailing dots
    // if string is empty after normalizing, return as it is invalid
    let sections: string = pageName.replace(/\s/g, '').replace(/^\.|\.$/g, '');
    if (sections === undefined || sections === null || sections.length === 0) {
      return [];
    }

    // split sections into array of strings, split on dot value ('.')
    return sections.split('.');
  }
}
