let modNoId = {
      aerodrome_category_name: '',
      charges_type: '',
      aerodrome_category_id: 10,
      new_charges_type: '',
      new_aerodromecategory_name: '',
      new_aerodromecategory_id: 10
};

let modWithId = {
  "id": 1
};

for (let item in modNoId) {
  if (modNoId.hasOwnProperty(item)) {
    modWithId[item] = modNoId[item];
  }
}
