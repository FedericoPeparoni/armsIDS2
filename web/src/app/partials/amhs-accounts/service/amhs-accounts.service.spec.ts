// interface
import { IAMHSAccount } from '../amhs-accounts.interface';

// service
import { AMHSAccountsService, endpoint } from './amhs-accounts.service';

describe('service AMHSAccountsService', () => {

    let httpBackend,
        restangular;

    let amhsAccount: IAMHSAccount = {
        id: 1,
        active: true,
	descr: 'some descr',
        addr: 'some addr',
        passwd: 'default',
	allow_mta_conn: true,
	svc_hold_for_delivery: true
    };

    beforeEach(angular.mock.module('armsWeb'));

    beforeEach(() => {
        inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
            httpBackend = $httpBackend;
            restangular = Restangular;

            $httpBackend.when('GET', (url: string): any => {
              if ('http://localhost:8080/api/system-configurations/noauth') {
                return true;
              };
            }).respond('ok');
        });
    });

    it('should be registered', inject((amhsAccountsService: AMHSAccountsService) => {
        expect(amhsAccountsService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of amhsAccounts', inject((amhsAccountsService: AMHSAccountsService) => {

            let expectedResponse = Array<IAMHSAccount>();

            amhsAccountsService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single amhsAccount', inject((amhsAccountsService: AMHSAccountsService) => {

            amhsAccountsService.create(amhsAccount);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, amhsAccount);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single amhsAccount', inject((amhsAccountsService: AMHSAccountsService) => {

            amhsAccount.id = 1;

            amhsAccountsService.update(amhsAccount, amhsAccount.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${amhsAccount.id}`)
                .respond(200, amhsAccount);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single amhsAccount', inject((amhsAccountsService: AMHSAccountsService) => {

            amhsAccount.id = 1;

            amhsAccountsService.delete(amhsAccount.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${amhsAccount.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
