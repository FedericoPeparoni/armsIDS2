// interfaces
import { ITown } from '../utilities-towns.interface';

// service
import { UtilitiesTownsService, endpoint } from './utilities-towns.service';

describe('service UtilitiesTownsService', () => {

    let httpBackend,
        restangular;

    let town: ITown = {
        id: null,
        town_or_village_name: 'Tinseltown',
        water_utility_schedule: null,
        residential_electricity_utility_schedule: null,
        commercial_electricity_utility_schedule: null
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

    it('should be registered', inject((utilitiesTownsService: UtilitiesTownsService) => {
        expect(utilitiesTownsService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of towns', inject((utilitiesTownsService: UtilitiesTownsService) => {

            let expectedResponse = Array<ITown>();

            utilitiesTownsService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single town', inject((utilitiesTownsService: UtilitiesTownsService) => {

            utilitiesTownsService.create(town);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, town);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single town', inject((utilitiesTownsService: UtilitiesTownsService) => {

            town.id = 1;

            utilitiesTownsService.update(town, town.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${town.id}`)
                .respond(200, town);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single town', inject((utilitiesTownsService: UtilitiesTownsService) => {

            town.id = 1;

            utilitiesTownsService.delete(town.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${town.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
