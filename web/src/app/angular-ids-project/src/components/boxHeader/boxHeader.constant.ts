// interfaces
import { IBoxHeaderUserGuide } from './boxHeader.interface';

export class BoxHeaderConstant {

  static userGuide: IBoxHeaderUserGuide = {
    date: new Date(2019, 5),
    location: 'assets/',
    name: 'guide',
    notes: 'Update after CAAB, KCAA, INAC, EANA and ZACL releases.',
    version: '1.1',
    content: [
      { identifier: '1', name: 'introduction', page: 16 },
      { identifier: '2', name: 'overview', page: 19 },
      { identifier: '3', name: 'arms-introduction', page: 26 },
      { identifier: '4', name: 'login', page: 36 },
      { identifier: '5', name: 'billing', page: 38, content: [
        { identifier: '5.1', name: 'invoice-management', page: 38 },
        { identifier: '5.2', name: 'transaction-management', page: 43, content: [
          {identifier: '5.2.1', name: 'background', page: 43 },
          {identifier: '5.2.2', name: 'interface', page: 43 },
          {identifier: '5.2.3', name: 'form', page: 45 }
        ] },
        { identifier: '5.3', name: 'pending-transaction-management', page: 46, content: [
          { identifier: '5.3.1', name: 'background', page: 46 },
          { identifier: '5.3.2', name: 'interface', page: 47 },
          { identifier: '5.3.3', name: 'form', page: 47 }
        ] },
        { identifier: '5.4', name: 'certificate-management', page: 48 },
        { identifier: '5.5', name: 'aviation-billing-engine', page: 49 },
        { identifier: '5.6', name: 'non-aviation-billing-engine', page: 53 },
        { identifier: '5.7', name: 'point-of-sale-invoice-generation', page: 55 },
        { identifier: '5.8', name: 'passenger-revenue-reconciliation-kcaa', page: 59 }
      ] },
      { identifier: '6', name: 'flight-data', page: 60, content: [
        { identifier: '6.1', name: 'flight-movement-management', page: 60, content: [
          {identifier: '6.1.1', name: 'general', page: 60 },
          {identifier: '6.1.2', name: 'data-grid-display-interface', page: 60 },
          {identifier: '6.1.3', name: 'data-grid-display-form', page: 62 },
          {identifier: '6.1.4', name: 'data-grid-functions', page: 66 },
          {identifier: '6.1.5', name: 'data-entry-form', page: 68 },
          {identifier: '6.1.6', name: 'gis-mapping-form', page: 69 },
          {identifier: '6.1.7', name: 'flight-plan-display-form', page: 71 }
        ] },
        { identifier: '6.2', name: 'flight-schedule-management', page: 71 },
        { identifier: '6.3', name: 'atc-movement-log-management', page: 73 },
        { identifier: '6.4', name: 'tower-movement-log-management', page: 75 },
        { identifier: '6.5', name: 'radar-flight-strip-management', page: 78 },
        { identifier: '6.6', name: 'passenger-service-charge-return-management', page: 80 },
        { identifier: '6.7', name: 'rejected-item-management', page: 82 },
        { identifier: '6.8', name: 'uploaded-file-management', page: 84 }
      ] },
      { identifier: '7', name: 'operations', page: 86, content: [
        { identifier: '7.1',  name: 'account-management', page: 86 },
        { identifier: '7.2',  name: 'aircraft-registration-management', page: 91 },
        { identifier: '7.3',  name: 'aircraft-type-management', page: 94 },
        { identifier: '7.4',  name: 'currency-and-exchange-rate-management', page: 95 },
        { identifier: '7.5',  name: 'nominal-route-management', page: 97 },
        { identifier: '7.6',  name: 'unspecified-aircraft-type-management', page: 98 },
        { identifier: '7.7',  name: 'unspecified-departure-and-destination-location-management', page: 100 }
      ] },
      { identifier: '8', name: 'charges-and-formulas', page: 103, content: [
        { identifier: '8.1',  name: 'air-navigation-charges-schedule-managment', page: 103 },
        { identifier: '8.2',  name: 'average-mtow-factor-managment', page: 106 },
        { identifier: '8.3',  name: 'enroute-air-navigation-charge-managment', page: 107 },
        { identifier: '8.4',  name: 'recurring-charge-managment', page: 110 },
        { identifier: '8.5',  name: 'service-charge-catalogue-managment', page: 112 },
        { identifier: '8.6',  name: 'utilities-schedule-managment', page: 114 },
        { identifier: '8.7',  name: 'utilities-town-and-village-managment', page: 116 },
        { identifier: '8.8',  name: 'flight-reassignment-managment', page: 117 }
      ] },
      { identifier: '9', name: 'exemptions', page: 121, content: [
        { identifier: '9.1',  name: 'exempt-account-management', page: 121 },
        { identifier: '9.2',  name: 'exempt-aircraft-type-management', page: 122 },
        { identifier: '9.3',  name: 'exempt-flight-route-management', page: 124 },
        { identifier: '9.4',  name: 'exempt-flight-status-management', page: 126 },
        { identifier: '9.5',  name: 'exempt-aircraft-and-flights-management', page: 128 },
        { identifier: '9.6',  name: 'repositioning-aerodrome-cluster-management', page: 130 },
        { identifier: '9.7',  name: 'aerodrome-service-outage-management', page: 132 },
        { identifier: '9.8',  name: 'discounted-aerodrome-charges-management', page: 134 }
      ] },
      { identifier: '10', name: 'management', page: 135, content: [
        { identifier: '10.1',  name: 'aerodrome-category-management', page: 135 },
        { identifier: '10.2',  name: 'aerodrome-management', page: 136 },
        { identifier: '10.3',  name: 'airpsace-management', page: 138, content: [
          { identifier: '10.3.1',  name: 'background', page: 138 },
          { identifier: '10.3.2',  name: 'interface', page: 138 },
          { identifier: '10.3.3',  name: 'form', page: 139 }
        ] },
        { identifier: '10.4',  name: 'banking-account-management', page: 139 },
        { identifier: '10.5',  name: 'interest-rates-management', page: 140 },
        { identifier: '10.6',  name: 'billing-centre-management', page: 143 },
        { identifier: '10.7',  name: 'country-management', page: 144 },
        { identifier: '10.8',  name: 'regional-country-management', page: 145 },
        { identifier: '10.9',  name: 'transaction-workflow-management', page: 147 },
        { identifier: '10.10',  name: 'application-management', page: 149 },
        { identifier: '10.11',  name: 'system-configuration-management', page: 150 },
        { identifier: '10.12',  name: 'cached-event-management', page: 151 },
        { identifier: '10.13',  name: 'plugin-management', page: 152 }
      ] },
      { identifier: '11', name: 'data-analysis-and-statistics', page: 155, content: [
        { identifier: '11.1',  name: 'system-summary', page: 155 },
        { identifier: '11.2',  name: 'analysis-and-statistics-air-traffic', page: 155 },
        { identifier: '11.3',  name: 'analysis-and-statistics-revenue', page: 157 },
        { identifier: '11.4',  name: 'aviation-volume-and-trend-analysis', page: 159 },
        { identifier: '11.5',  name: 'flight-frequency-analysis', page: 160 },
        { identifier: '11.6',  name: 'revenue-projection', page: 160 },
        { identifier: '11.7',  name: 'report-generation', page: 161 }
      ] },
      { identifier: '12', name: 'templates', page: 170, content: [
        { identifier: '12.1',  name: 'invoice-templates', page: 170 },
        { identifier: '12.2',  name: 'report-templates', page: 171 }
      ] },
      { identifier: '13', name: 'security', page: 173, content: [
        { identifier: '13.1',  name: 'password-change', page: 173 },
        { identifier: '13.2',  name: 'groups', page: 174 },
        { identifier: '13.3',  name: 'users', page: 176 },
        { identifier: '13.4',  name: 'user-event-log', page: 178 },
        { identifier: '13.5',  name: 'sessions', page: 179 }
      ] },
      { identifier: '14', name: 'help', page: 181 },
      { identifier: '15', name: 'troubleshooting-user-issues', page: 182 },
      { identifier: '16', name: 'data-file-formats', page: 184 },
      { identifier: '17', name: 'operational-scenarios', page: 193 }
    ]
  };
}
