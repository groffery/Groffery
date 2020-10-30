import React, { useState, useEffect } from "react";
import { Form } from "react-bootstrap";
import "./Flyers.css";
import demoImage from "../../assets/image.png";
import { CCol, CRow, CContainer, CCard, CCardBody } from "@coreui/react";

import { Document, Page, pdfjs } from "react-pdf";

function Flyers() {
  const [image, setImage] = useState(null);
  console.log(image);
  const [check, setCheck] = useState(false);
  const [city, setCity] = useState(null);
  const [area, setArea] = useState(null);
  const [store, setStore] = useState(null);
  const [responseData, setResponseData] = useState({});
  console.log(responseData);
  const [numPages, setNumPages] = useState(null);
  const [pageNumber, setPageNumber] = useState(1);

  const buttonDisabled = !image || !city || !area || !store;
  const ImageStyle = {
    width: "200px",
    height: "150px",
  };
  pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;

  const uploadProduct = () => {
    var url = image;
    var filename = url.name;
    console.log(filename);
    fetch(
      `https://7ryh4my3sh.execute-api.us-east-1.amazonaws.com/dev/groffery-flyers?file=${filename}`
    )
      .then((res) => res.json())
      .then((data) => {
        const ChangeObject = Object.values(data.URL.fields);
        console.log(ChangeObject);
        const formData = new FormData();

        console.log("key", ChangeObject[0]);
        console.log("AWSAccessKeyId", ChangeObject[1]);
        console.log("x-amz-security-token", ChangeObject[2]);
        console.log("policy", ChangeObject[3]);
        console.log("signature", ChangeObject[4]);

        formData.append("key", ChangeObject[0]);
        formData.append("AWSAccessKeyId", ChangeObject[1]);
        formData.append("x-amz-security-token", ChangeObject[2]);
        formData.append("policy", ChangeObject[3]);
        formData.append("signature", ChangeObject[4]);
        formData.append("file", image);

        fetch(`${data.URL.url}`, {
          method: "POST",
          body: formData,
          headers: {
            "Content-type": "multipart/form-data",
          },
        }).then((res) => console.log(res));
      });
  };

  function onDocumentLoadSuccess({ numPages }) {
    setNumPages(numPages);
  }

  return (
    <CContainer fluid>
      <CRow className="justify-content-md-center">
        <CCol sm="8">
          <CCard>
            <CCardBody>
              <CRow className="align-items-center">
                <CCol lg="6">
                  <Document file={image} onLoadSuccess={onDocumentLoadSuccess}>
                    <Page pageNumber={pageNumber} />
                  </Document>
                </CCol>
                <CCol lg="6">
                  <Form>
                    <Form.File
                      id="custom-file-translate-scss"
                      label="Select Image"
                      lang="en"
                      custom
                      onChange={(e) => setImage(e.target.files[0])}
                    />
                  </Form>
                </CCol>
              </CRow>
              <CRow className="my-4">
                <CCol lg="4">
                  <div className="city">
                    <select
                      disabled={check ? true : false}
                      required
                      name="city"
                      className="form-control"
                      value={city}
                      onChange={(e) => setCity(e.target.value)}
                    >
                      <option value="null">City</option>
                      <option value="dubai">Dubai</option>
                      <option value="london">London</option>
                      <option value="birmingham">Birmingham</option>
                      <option value="glasgow">Glasgow</option>
                    </select>
                  </div>
                </CCol>
                <CCol lg="4">
                  <div className="area">
                    <select
                      disabled={check ? true : false}
                      required
                      name="area"
                      className="form-control"
                      value={area}
                      onChange={(e) => setArea(e.target.value)}
                    >
                      <option value="null">Area</option>
                      <option value="scotland">Scotland</option>
                      <option value="northern-ireland">Northern Ireland</option>
                      <option value="wales">Wales</option>
                      <option value="north-east">North East</option>
                    </select>
                  </div>
                </CCol>
                <CCol lg="4">
                  <div className="store">
                    <select
                      disabled={check ? true : null}
                      required
                      name="city"
                      className="form-control"
                      value={store}
                      onChange={(e) => setStore(e.target.value)}
                    >
                      <option value="city">Store</option>
                      <option value="locksmith">locksmith</option>
                      <option value="loswithiel">Lostwithiel</option>
                      <option value="florists">florists</option>
                    </select>
                  </div>
                </CCol>
              </CRow>
              <CRow>
                <CCol lg="12">
                  <div className="d-flex align-items-center justify-content-center">
                    <input
                      onChange={(e) => setCheck(e.target.checked && true)}
                      className="check form-control"
                      type="checkbox"
                      id="checkbox"
                      disabled={
                        city === null || area === null || store === null
                          ? false
                          : true
                      }
                    />
                    <label style={{ marginBottom: 0 }} htmlFor="checkbox">
                      {" "}
                      Upload for all the stores
                    </label>
                  </div>
                </CCol>
              </CRow>
            </CCardBody>
          </CCard>
          <div className="button-section my-5 text-center">
            <button
              onClick={uploadProduct}
              style={{ background: check ? !image : buttonDisabled && "gray" }}
              disabled={check ? !image : buttonDisabled}
              className="upload"
            >
              Upload
            </button>
            <button
              style={{ borderColor: check ? !image : buttonDisabled && "gray" }}
              disabled={check ? !image : buttonDisabled}
              className="schedule"
            >
              Schedule
            </button>
          </div>
        </CCol>
      </CRow>
    </CContainer>
  );
}

export default Flyers;
