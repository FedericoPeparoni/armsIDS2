export interface IScCreditPayment {
    id: number,
    transactionTime: string,
    account: any,
    requestorIp: string,
    request: string,
    response: string,
    responseStatus: string,
    responseDescription: string,
    version: number
}