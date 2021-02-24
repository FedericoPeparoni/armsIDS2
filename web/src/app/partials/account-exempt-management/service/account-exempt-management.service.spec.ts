// interfaces
import { IAccountExemptManagement } from '../account-exempt-management.interface';

// service
import { AccountExemptManagementService, endpoint } from './account-exempt-management.service';

describe('service AccountExemptManagementService', () => {

    let httpBackend,
        restangular;

    let exemptAccount: IAccountExemptManagement = {
        account_id: 4,
        account_name: 'Test',
        enroute: 100,
        parking: 100,
        flight_notes: 'Mock Flight Notes',
        approach_fees_exempt: 0,
        aerodrome_fees_exempt: 0,
        late_arrival: 0,
        late_departure: 0,
        international_pax: 0,
        domestic_pax: 0,
        extended_hours: 0
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

    it('should be registered', inject((accountExemptManagementService: AccountExemptManagementService) => {
        expect(accountExemptManagementService).not.toEqual(null);
    }));

    describe('list', () => {

        it('should return an array of accounts with exemptions', inject((accountExemptManagementService: AccountExemptManagementService) => {

            let expectedResponse = Array<IAccountExemptManagement>();

            accountExemptManagementService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {

        it('should create a single exempt account record', inject((accountExemptManagementService: AccountExemptManagementService) => {

            accountExemptManagementService.create(exemptAccount);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, exemptAccount);

            httpBackend.flush();

        }));


    });

    describe('update', () => {
        it('should be registered', inject((accountExemptManagementService: AccountExemptManagementService) => {
            expect(accountExemptManagementService.update).not.toEqual(null);
        }));

        it('should update a single exempt account record', inject((accountExemptManagementService: AccountExemptManagementService) => {

            exemptAccount.id = 1;

            accountExemptManagementService.update(exemptAccount, exemptAccount.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAccount.id}`)
                .respond(200, exemptAccount);

            httpBackend.flush();

        }));
    });

    describe('delete', () => {

        it('should delete a single exempt account record', inject((accountExemptManagementService: AccountExemptManagementService) => {

            exemptAccount.id = 1;

            accountExemptManagementService.delete(exemptAccount.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAccount.id}`)
                .respond(200, null);

            httpBackend.flush();

        }));
    });
});
