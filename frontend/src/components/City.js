import React, { useState, useEffect } from "react";
import CityDataService from "../services/CityService";

import AuthService from "../services/auth.service";


const currentUser = AuthService.getCurrentUser();

const City = props => {
  const initialCityState = {
    id: null,
    name: "",
    photo: "",
  };
  const [currentCity, setCurrentCity] = useState(initialCityState);
  const [message, setMessage] = useState("");

  const getCity = id => {
    CityDataService.get(id)
        .then(response => {
          setCurrentCity(response.data);
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
  };

  useEffect(() => {
    getCity(props.match.params.id);
  }, [props.match.params.id]);

  const handleInputChange = event => {
    const { name, value } = event.target;
    setCurrentCity({ ...currentCity, [name]: value });
  };

  const updateCity = () => {
    CityDataService.update(currentCity.id, currentCity)
        .then(response => {
          console.log(response.data);
          setMessage("The city was updated successfully!");
          props.history.push("/cities");
        })
        .catch(e => {
          console.log(e);
        });
  };

  if (currentUser) {
    if (currentUser?.roles.includes('ROLE_USER') || currentUser?.roles.includes('ROLE_ADMIN')) {
      return (
          <div>
            <div className="edit-form">
              <h4>Access Forbidden</h4>
            </div>
          </div>
      );
    } else {

      return (
          <div>
            {currentCity ? (
                <div className="edit-form">
                  <h4>City</h4>
                  <form>
                    <div className="form-group">
                      <label htmlFor="name">Name</label>
                      <input
                          type="text"
                          className="form-control"
                          id="name"
                          name="name"
                          value={currentCity.name}
                          onChange={handleInputChange}
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="photo">Photo</label>
                      <input
                          type="text"
                          className="form-control"
                          id="photo"
                          name="photo"
                          value={currentCity.photo}
                          onChange={handleInputChange}
                      />
                    </div>

                  </form>

                  <button
                      type="submit"
                      className="btn btn-info"
                      onClick={updateCity}
                  >
                    Update
                  </button>
                  <p>{message}</p>
                </div>
            ) : (
                <div>
                  <br/>
                  <p>Please click on a City...</p>
                </div>
            )}
          </div>
      );
    }
  };


};

export default City;
