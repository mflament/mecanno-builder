interface ResponseTypeMap {
  arraybuffer: ArrayBuffer;
  blob: Blob;
  document: HTMLDocument | XMLDocument;
  json: any;
  text: string;
}

export class HttpClient {
  get<T extends keyof ResponseTypeMap, R extends ResponseTypeMap[T]>(
    uri: string,
    responseType: XMLHttpRequestResponseType = 'json'
  ): Promise<R> {
    return new Promise((resolve, reject) => {
      const request = new XMLHttpRequest();
      request.open('GET', uri);
      request.responseType = responseType;
      request.onload = () => {
        if (request.status === 200) {
          resolve(request.response);
        } else {
          reject(new Error("get '" + uri + "' error " + request.status + ' : ' + request.responseText));
        }
      };
      request.onerror = reject;
      request.send();
    });
  }
}
