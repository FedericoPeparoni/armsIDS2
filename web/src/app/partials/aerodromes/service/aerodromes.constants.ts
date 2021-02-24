export enum CoordinateLabels {
    DECIMALLAT = <any>'[-]DD.ddddd',
    DECIMALLON = <any>'[-]DDD.ddddd',
    DMSLAT = <any>'DD MM SS[N|S]',
    DMSLON = <any>'DDD MM SS[E|W]'
}

export enum CoordinateValidation {
    LATREGEX = <any>/(^[-0-9]{0,3})+(\.[0-9]{0,5})$|^([0-9]{2,3}(°|\s))([0-9]{2}(′|\'|\s))([0-9]{2}(″|\")?)(\.[0-9]{1,2})?(N|S)$/,
    LONREGEX = <any>/(^[-0-9]{0,3})+(\.[0-9]{0,5})$|^([0-9]{3}(°|\s)?)([0-9]{2}(′|\'|\s)?)([0-9]{2}(″|\")?)(\.[0-9]{1,2})?(E|W)$/
}
