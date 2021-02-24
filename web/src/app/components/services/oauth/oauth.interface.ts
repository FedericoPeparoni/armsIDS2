export interface Ioauth {
  access_token: string;
  expires_in: number;
  scope: string;
  token_type: string;
}

export interface IoauthError {
  error: string;
  error_description: string;
}
