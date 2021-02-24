/**
 * Wraps all highlight text found within the expresion value with an element.
 * 
 * Default element is a `span` with the classes `text-warning ng-
 */

/** @ngInject */
export function highlight(): (val: string, highlight: string, cssClass?: string) => string {

    return function (val: string, highlight: string, cssClass?: string, element?: string): string {

        // if no higlight value, return original value supplied as is
        if (!highlight) {
            return val;
        }

        // if no cssClass value, set to default
        if (!cssClass) {
            cssClass = 'text-warning bg-warning';
        }

        // if no element value, set to default
        if (!element) {
            element = 'span';
        }

        // replace all instances of highlight value found in text with 
        // wrapped value using `element` and `cssClass` values
        return val.replace(new RegExp(highlight, 'gi'),
            '<' + element + ' class="' + cssClass + '">$&</' + element  + '>');
    };
}
