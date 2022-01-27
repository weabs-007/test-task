import React, { useState, useEffect, useMemo, useRef } from "react";
import CityDataService from "../services/CityService";
import { useTable } from "react-table";
import Pagination from "../components/Pagination";
import AuthService from "../services/auth.service";

let initialState = {hiddenColumns: [""]};
// console.log("before", initialState);

const currentUser = AuthService.getCurrentUser();

// if (currentUser !== undefined) {
if (currentUser) {
  if (currentUser?.roles.includes('ROLE_USER') || currentUser?.roles.includes('ROLE_ADMIN')) {
    initialState = {hiddenColumns: ["Actions"]};
  }
  // console.log("after", initialState);
}


const CitiesList = (props) => {
  const [cities, setCities] = useState([]);
  const [searchTitle, setSearchTitle] = useState("");
  const citiesRef = useRef();

  const [currentPage, setCurrentPage] = useState(1);
  const [postsPerPage] = useState(40);

  citiesRef.current = cities;

  useEffect(() => {
    retrieveCities();
  }, []);

  // Get current posts
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = cities.slice(indexOfFirstPost, indexOfLastPost);

  // Change page
  const paginate = pageNumber => setCurrentPage(pageNumber);

  const onChangeSearchTitle = (e) => {
    const searchTitle = e.target.value;
    setSearchTitle(searchTitle);
  };

  const retrieveCities = () => {
    CityDataService.getAll()
        .then((response) => {
          setCities(response.data);
        })
        .catch((e) => {
          console.log(e);
        });
  };

  const findByTitle = () => {
    CityDataService.findByTitle(searchTitle)
        .then((response) => {
          setCities(response.data);
        })
        .catch((e) => {
          console.log(e);
        });
  };

  const openCity = (rowIndex) => {
    const id = citiesRef.current[rowIndex].id;

    props.history.push("/cities/" + id);
  };

  const columns = useMemo(
      () => [
        {
          Header: "Id",
          accessor: "id",
        },{
          Header: "Name",
          accessor: "name",
        },
        {
          Header: "Description",
          accessor: "photo",
          Cell: (props) => {
            const photo = props.row.original.photo;
            return (
                <div>
                  <img className="photo"
                       src={`${photo}`}
                       alt=""
                  />
                </div>
            );
          },
        },
        {
          Header: "Actions",
          accessor: "Actions",
          Cell: (props) => {
            const rowIdx = props.row.id;
            return (
                <div>
                  <button className="btn btn-sm btn-outline-warning" onClick={() => openCity(rowIdx)}>
                    Edit
                  </button>
                </div>
            );
          },
        },
      ],
      // eslint-disable-next-line
      []
  );



  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow,
  } = useTable({
    columns,
    initialState,
    data: currentPosts,
  });

  return (
      <div className="list row">
        <div className="col-md-8">
          <div className="input-group mb-3">
            <input
                type="text"
                className="form-control"
                placeholder="Search by city name"
                value={searchTitle}
                onChange={onChangeSearchTitle}
            />
            <div className="input-group-append">
              <button
                  className="btn btn-outline-secondary"
                  type="button"
                  onClick={findByTitle}
              >
                Search
              </button>
            </div>
          </div>
        </div>
        <div className="col-md-12 list">
          <table
              className="table table-striped table-bordered"
              {...getTableProps()}
          >
            <thead>
            {headerGroups.map((headerGroup) => (
                <tr {...headerGroup.getHeaderGroupProps()}>
                  {headerGroup.headers.map((column) => (
                      <th {...column.getHeaderProps()}>
                        {column.render("Header")}
                      </th>
                  ))}
                </tr>
            ))}
            </thead>
            <tbody {...getTableBodyProps()}>
            {rows.map((row, i) => {
              prepareRow(row);
              return (
                  <tr {...row.getRowProps()}>
                    {row.cells.map((cell) => {
                      return (
                          <td {...cell.getCellProps()}>{cell.render("Cell")}</td>
                      );
                    })}
                  </tr>
              );
            })}
            </tbody>
          </table>
          <Pagination
              postsPerPage={postsPerPage}
              totalPosts={cities.length}
              paginate={paginate}
          />
        </div>

      </div>
  );
};

export default CitiesList;
