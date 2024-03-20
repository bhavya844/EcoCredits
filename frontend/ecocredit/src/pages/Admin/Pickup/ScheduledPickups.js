import React, { useState, useEffect } from "react";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Table from "react-bootstrap/Table";
import Button from "react-bootstrap/Button";
import Cookies from "js-cookie";
import "./SchedulePickups.css";

const ScheduledPickups = () => {
    const [pickups, setPickups] = useState([]);

  useEffect(() => {
    fetch(
      `${process.env.REACT_APP_BASE_URL}/admin/scheduled-pickups`,
      {
        method: "GET",
        mode: "cors",
        headers: {
          Authorization: `Bearer ${Cookies.get("token")}`,
          Accept: "application/json",
        },
      }
    )
      .then((response) => response.json())
      .then((data) => {
        setPickups(data);
      })
      .catch((error) =>
        console.error("Fetching pickup status data failed:", error)
      );
  }, []);

return (
    
    <Container fluid className="background-image">
      <Row className="d-flex justify-content-center align-items-center">
        <Col lg={8} className="mt-5">
          <div className="shadow-lg rounded-4 bg-ec-grey p-4 mb-5">
            <h1 className="text-center text-ec-dark-green p-2">
              Scheduled Pickups
            </h1>
            <div class="table-wrapper">
            <Table
              bordered
              hover
              variant="ec-grey"
              className="table-custom mb-0"
            >
              <thead>
                <tr>
                  <th>#</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>UserID</th>
                  <th>Waste</th>
                </tr>
              </thead>
              <tbody>
                {pickups.map((pickup, index) => (
                  <tr key={pickup.id}>
                    <td>{index + 1}</td>
                    <td>{pickup.date}</td>
                    <td>{pickup.time}</td>
                    <td>{pickup.userId}</td>
                    <Table
                        bordered
                        hover
                        variant="ec-grey"
                        className="table-custom mb-0">
                        <tbody>
                            {pickup.wastes.map((waste) => (
                                <tr key={waste.wasteId}>
                                    <td className="col-widths-category">{waste.category}</td>
                                    <td className="col-widths-weight">{waste.weight ? waste.weight : 0.0} kg</td>
                                    <td className="col-widths-button">
                                        <Button variant="ec-dark-green text-ec-grey" size="sm">
                                            Update
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                  </tr>
                ))}
              </tbody>
            </Table>
            </div>
          </div>
        </Col>
      </Row>
    </Container>
    );
};


export default ScheduledPickups;