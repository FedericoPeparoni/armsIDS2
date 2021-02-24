// wrap around contents of a modal, just inside the script tags, and it will become draggable

/** @ngInject */
export function modalMovable($document: ng.IDocumentService): angular.IDirective {
    return {
        restrict: 'E',
        link: function(scope: ng.IScope, elem: ng.IAugmentedJQuery, attrs: ng.IAttributes): void {
            let startX: number = 0;
            let startY: number = 0;
            let x: number = 0;
            let y: number = 0;

            var dialogWrapper = elem.parent();

            dialogWrapper.css({
                position: 'relative'
            });

            dialogWrapper.on('mousedown', function(event: any): void {
                // prevent default dragging of selected content
                event.preventDefault();
                startX = event.pageX - x;
                startY = event.pageY - y;
                $document.on('mousemove', mousemove);
                $document.on('mouseup', mouseup);
            });

            function mousemove(event: any): void {
                y = event.pageY - startY;
                x = event.pageX - startX;
                dialogWrapper.css({
                    top: y + 'px',
                    left: x + 'px'
                });
            }

            function mouseup(): void {
                $document.unbind('mousemove', mousemove);
                $document.unbind('mouseup', mouseup);
            }
        }
    };
}
