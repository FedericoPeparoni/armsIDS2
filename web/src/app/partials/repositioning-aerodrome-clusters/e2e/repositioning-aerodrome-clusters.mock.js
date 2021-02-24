let clusterWithNoId = {
  repositioning_aerodrome_cluster_name: 'Test Cluster',
  enroute_fees_are_exempt: false,
  approach_fees_are_exempt: false,
  aerodrome_fees_are_exempt: false,
  late_arrival_fees_are_exempt: false,
  late_departure_fees_are_exempt: false,
  parking_fees_are_exempt: false,
  adult_passenger_fees_are_exempt: false,
  child_passenger_fees_are_exempt: false,
  aerodrome_identifier: ['Test Aerodrome'],
  flight_notes: 'Test Flight Notes'
};

let clusterWithId = {
  id: 1
};

let unknownAerodrome = {
    id: 1,
    text_identifier: 'test',
    name: 'Test Unknown Aerodrome',
    maintained: true,
    aerodrome_identifier: null,
    latitude: null,
    longitude: null,
    status: null
}

Object.assign(clusterWithId, clusterWithNoId);

module.exports = [
  {
    request: {
      path: '/unspecified-departure-destination-locations',
      method: 'GET'
    },
    response: {
      data: {
        content: [unknownAerodrome]
      }
    }
  },
  {
    request: {
      path: '/repositioning-aerodrome-clusters',
      method: 'GET'
    },
    response: {
      data: {
        content: [clusterWithId]
      }
    }
  },
  {
    request: {
      path: '/repositioning-aerodrome-clusters',
      method: 'POST',
      data: clusterWithNoId
    },
    response: {
      data: {
        content: [clusterWithId]
      }
    }
  },
  {
    request: {
      path: '/repositioning-aerodrome-clusters',
      method: 'PUT',
      data: clusterWithId
    },
    response: {
      data: {
        content: [clusterWithId]
      }
    }
  },
  {
    request: {
      path: '/repositioning-aerodrome-clusters/1',
      method: 'DELETE'
    },
    response: ''
  }
];
