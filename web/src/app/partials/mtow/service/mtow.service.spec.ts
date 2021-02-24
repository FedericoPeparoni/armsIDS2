// interface
import { IMtowType } from '../mtow.interface';

// service
import { MtowService, endpoint } from './mtow.service';

describe('service MtowService', () => {

    let httpBackend,
        restangular;


    let mtow: IMtowType = {
        upper_limit: 1.11,
        average_mtow_factor: 1.111,
        factor_class: 'DOMESTIC'
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

    it('should be registered', inject((mtowService: MtowService) => {
        expect(mtowService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of mtow factors', inject((mtowService: MtowService) => {

            let expectedResponse = Array<IMtowType>();

            mtowService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single mtow factor', inject((mtowService: MtowService) => {

            mtowService.create(mtow);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, mtow);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single mtow factor', inject((mtowService: MtowService) => {

            mtow.id = 1;

            mtowService.update(mtow, mtow.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${mtow.id}`)
                .respond(200, mtow);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single mtow factor', inject((mtowService: MtowService) => {

            mtow.id = 1;

            mtowService.delete(mtow.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${mtow.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
