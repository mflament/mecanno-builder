import ReactDOM from 'react-dom';
import React, { Component, ReactElement } from 'react';
import { HttpClient } from './utils/HttpUtils';

const httpClient = new HttpClient();

interface PartsRepository {}

interface ApplicationProps {}
interface ApplicationState {
  parts?: PartsRepository;
}

class Application extends Component<ApplicationProps, ApplicationState> {
  constructor(props: ApplicationProps) {
    super(props);
    this.state = {};
  }

  async componentDidMount(): Promise<void> {
    const parts = await httpClient.get('data/parts.json');
    this.setState({ parts: parts });
  }

  componentWillUnmount(): void {}

  render(): ReactElement {
    const data = this.state.parts;
    if (data) return <div>Loaded</div>;
    return <div className="loading">Loading...</div>;
  }
}

ReactDOM.render(<Application />, document.getElementById('application'));
