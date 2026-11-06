FROM node:8

ENV NODE_ENV dev

RUN mkdir /cpr
COPY ./ /cpr/
RUN rm -rRf /cpr/node_modules
RUN cd /cpr && npm install
WORKDIR /cpr
ENTRYPOINT cd /cpr && npm test
