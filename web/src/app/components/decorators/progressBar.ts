/** @ngInject */
export const ProgressBarDecorator = ($provide: ng.IScope) => {
    $provide.decorator('cfpLoadingBar', ($delegate: ng.IScope, $timeout: ng.ITimeoutService, $rootScope: any) => {

        /**
         * 
         * We use 30 minutes as a base case to handle long requests.
         * 30 mins = 1800 seconds = 0.00054/s to reach 0.98
         * 
         */
        const ONE_SECOND_PROGRESS = 0.00054;

        let incTimeout;
        let set;

        const inc = () => {
            const currentStatus = $delegate.status();

            // skip if finished or not started
            if (currentStatus >= 1 || currentStatus <= 0) { return; }

            // prevent increment after 98% percent
            const amountToInc = currentStatus <= 0.98 ? currentStatus + ONE_SECOND_PROGRESS : 0;

            set(amountToInc);
        };

        // use the set function found in cfpLoadingBar but call our own inc function
        const packageSet = $delegate.set;
        set = function(n: number): void {
            packageSet.apply(this, arguments);

            $timeout.cancel(incTimeout);

            incTimeout = $timeout(() => {
                inc();
            }, 1000);
        };

        // set loading bar to complete before state change
        $rootScope.$on('$stateChangeStart', () => $delegate.complete());
        $delegate.set = set;

        // return augmented cfpLoadingBar
        return $delegate;
    });
};
