// interfaces
import { IJobStatus } from '../aviation-billing-engine/aviation-billing-engine.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IAviationBillingEngine } from '../aviation-billing-engine/aviation-billing-engine.interface';
import { ICalculation } from './calculation.interface';
import { ServerDatetimeService } from '../../angular-ids-project/src/components/server-datetime/server-datetime.service';

/** @ngInject */
export class Calculation implements ICalculation {

    private queued: string = 'QUEUED';
    private started: string = 'STARTED';
    private completed: string = 'COMPLETED';
    private failed: string = 'FAILED';
    private currentTime: any = null;

    constructor(private job: string, private execute: string, private cancel: string, private status: string, private complete: string,
        private $scope: any, private service: any, private $interval: ng.IIntervalService,
        private serverDatetimeService: ServerDatetimeService) {
            serverDatetimeService.getCurrentDateTime('util/current-datetime').then((serverDateTime: string) => {
                this.currentTime = serverDateTime ? new Date(serverDateTime) : Date.now();
                $interval(() => { this.currentTime.setSeconds(this.currentTime.getSeconds() + 1); }, 1000);
            });
        }

    public checkStatusOnPageLoad(): ng.IPromise<boolean> {
        return this.service[this.status]().then((job: IJobStatus) => {
            const inProgress = job.job_execution_status === this.started || job.job_execution_status === this.queued;

            if (inProgress) {
                this.updateCalculationStatus();
            }

            return inProgress;
        });
    }

    public executeCalculation(data: string | IAviationBillingEngine): void {
        this.service[this.execute](data)
            .then(() => {
                this.$scope[this.job] = {
                    job_execution_status: 'STARTING',
                    job_parameters: '...',
                    start_time: '...',
                    remaining: '...',
                    steps_to_process: '...',
                    steps_completed: '...',
                    total_steps: '...',
                    etc_time: '...',
                    rate: '...'
                };

                this.clearAndStartInterval();
            })
            .catch((error: IRestangularResponse) => {
                if (!error.data) {
                    this.$scope.error = {
                        error: {
                            data: {
                                error: 'Asynchronous job failed',
                                error_description: 'Unknown reason'
                            }
                        }
                    };
                } else {
                    this.$scope.error = { error: error };
                }

                this.$scope.processing = false;
            });
    }

    public cancelCalculation(): ng.IPromise<any> {
        this.$scope.processing = false;

        return this.service[this.cancel]()
            .catch((error: IRestangularResponse) => this.$scope.error = { error: error });
    }

    // safety to avoid two intervals
    private updateCalculationStatus(): void {
        this.service[this.status]()
            .then((job: IJobStatus) => {
                this.$scope[this.job] = this.parseJob(job);
                this.clearAndStartInterval();
            })
            .catch((error: IRestangularResponse) => this.$scope.error = { error: error });
    }

    private clearAndStartInterval(): void {
        this.$interval.cancel(this.$scope.calculatePromise);
        this.startRefreshCalculationStatus();
    }

    private startRefreshCalculationStatus(): void {
        this.$scope.calculatePromise = this.$interval(() => {
            this.service[this.status]()
                .then((job: IJobStatus) => {
                    this.$scope[this.job] = this.parseJob(job);

                    // cancels the timer function
                    if (job.job_execution_status !== this.started && job.job_execution_status !== this.queued) {
                        this.$interval.cancel(this.$scope.calculatePromise);
                    }

                    // update processing flag
                    if (job.job_execution_status === this.completed || job.job_execution_status === this.failed) {
                        this.$scope.processing = false;
                    }

                    // runs a function after complete if one is provided (can be null if no post-processing required)
                    if (job.job_execution_status === this.completed && this.complete) {
                        this.service[this.complete](this.$scope.editable.preview, this.$scope.editable.iataInvoice)
                            .then((resp: any) => this.$scope.preview = resp);
                    }
                })
                .catch((error: IRestangularResponse) => {
                    this.$scope.error = { error: error };
                    this.$interval.cancel(this.$scope.calculatePromise);
                });
        }, 3000);
    }

    /**
     * The params of the job comes back as a string.
     * This extracts either the flight ID/s or the month
     * and year from that string, to display to the user
     *
     * @param  {IJobStatus} job
     * @returns IJobStatus
     */
    private parseJob(job: IJobStatus): IJobStatus {
        if (job.variables) {
            const paramsArr: string[] = job.variables.split(';');

            for (let i = 0, len = paramsArr.length; i < len; i++) {
                const obj = paramsArr[i].split('=');

                job[obj[0]] = obj[1];
            }
        }

        const startTime: any = new Date(job.start_time);
        const runningTime = (this.currentTime - startTime) / 1000;
        const recordsPerSecond = job.steps_completed / runningTime;
        let secondsLeft = job.steps_to_process / recordsPerSecond;

        if (isFinite(secondsLeft)) {
          job.seconds_left = this.convertSecondsToDHMS(secondsLeft);
        } else {
          secondsLeft = 0;
        }

        // set Estimated Time of Completion
        const etcTime = new Date(this.currentTime);
        etcTime.setSeconds(etcTime.getSeconds() + secondsLeft);
        job.etc_time = etcTime.toISOString();

        job.rate = `${recordsPerSecond.toFixed(2)} rec/sec (or ${(recordsPerSecond * 60).toFixed(2)} records / minute)`;

        if (job.job_parameters) {
            if (job.job_parameters.indexOf('flightId') !== -1) {
                job.job_parameters = this.parseJobParams(job.job_parameters, 'Id');
            } else if (job.job_parameters.indexOf('startDate') !== -1 && job.job_parameters.indexOf('endDate') !== -1) {
                const startDate: string = this.parseJobParams(job.job_parameters, 'startDate');
                const endDate: string = this.parseJobParams(job.job_parameters, 'endDate');
                job.job_parameters = `${startDate} - ${endDate}`;
            }
        }

        if (job.message && job.message.includes('{{flights}}') && job.variables.includes('flights=')) {
            job.message = job.message.replace('{{flights}}', job.flights);
        }
        return job;
    }

    private convertSecondsToDHMS(seconds: number): string {
        seconds = Number(seconds);
        const d = this.str_pad(Math.floor(seconds / (3600 * 24)));
        const h = this.str_pad(Math.floor(seconds % (3600 * 24) / 3600));
        const m = this.str_pad(Math.floor(seconds % 3600 / 60));
        const s = this.str_pad(Math.floor(seconds % 60));
        return `${d}:${h}:${m}:${s}`;
    }

    /**
     * Prepends `0` to a
     * single digit number
     *
     * @param  {number} n
     * @returns string
     */
    private str_pad(n: number): string {
        return String('0' + n).slice(-2);
    }

    /**
     * This will parse IJobStatus.job_parameters for the value of the key supplied.
     * This assumes that the supplied params is a list of key/value pairs seperated
     * by a semicolon in the form of a string.
     *
     * Lastly, it will ignore any array (square bracket) characters and simply return
     * the comma seperated value without the square bracket characters.
     *
     * @param params key/value list to parse
     * @param key key of value to return
     */
    private parseJobParams(params: string, key: string): string {
        let paramsList: string[] = params.split(';');
        for (let i = 0; i < paramsList.length; i++) {
            if (paramsList[i].indexOf(key) !== -1) {
                let param = paramsList[i].replace(/\[|]/g, '').split('=')[1];
                if (paramsList[i].includes('startDate')) {
                    return moment(param, 'YYYY-MM-DDHH').format('L');
                } else if (paramsList[i].includes('endDate')) {
                    const endDate = moment(param, 'YYYY-MM-DDHH');

                    // api returns increemted day when RECALCULATING but not when GENERATING or PREVIEWING
                    if (params.toLowerCase().includes('invoice')) {
                        return endDate.format('L');
                    } else {
                        return endDate.subtract(1, 'days').format('L');
                    }
                } else {
                    return param;
                }
            }
        }
        return null;
    }
}
