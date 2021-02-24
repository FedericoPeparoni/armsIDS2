/**
 * This is simply a wrapper for window location properties
 * so it can be injected as a dependency and mocked for
 * testibility.
 * 
 * Added because the angularjs wrapper for $window
 * cannot be used in UsersService since users.html contains
 * data-export.directive which depp copies the UsersService using 
 * `angular.copy(...)` BUT $window and $scope cannot be 
 * deep copied because of cyclical and self references.
 * 
 * See https://code.angularjs.org/1.5.3/docs/error/ng/cpws
 */
export class WindowLocationService implements Location {

    public get hash(): string {
        return window.location.hash;
    }

    public get host(): string {
        return window.location.host;
    }

    public get hostname(): string {
        return window.location.hostname;
    }

    public get href(): string {
        return window.location.href;
    }

    public get origin(): string {
        return window.location.origin;
    }

    public get pathname(): string {
        return window.location.pathname;
    }

    public get port(): string {
        return window.location.port;
    }

    public get protocol(): string {
        return window.location.protocol;
    }

    public get search(): string {
        return window.location.search;
    }

    public assign(url: string): void {
        window.location.assign(url);
    }

    public reload(forcedReload?: boolean): void {
        window.location.reload(forcedReload);
    }

    public replace(url: string): void {
        window.location.replace(url);
    }

    public toString(): string {
        return window.location.toString();
    }
}
