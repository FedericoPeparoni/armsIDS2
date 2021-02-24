let testAccount = {
  id: 1,
  name: "Test Account"
};

module.exports = [
  {
    request: {
      path: '/accounts',
      method: 'GET'
    },
    response: {
      data: { content: [testAccount] }
    }
  }
];