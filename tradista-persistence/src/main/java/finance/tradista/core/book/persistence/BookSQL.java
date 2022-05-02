package finance.tradista.core.book.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import finance.tradista.core.book.model.Book;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.common.persistence.db.TradistaDB;
import finance.tradista.core.legalentity.persistence.LegalEntitySQL;

/*
 * Copyright 2015 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

public class BookSQL {

	public static Book getBookById(long id) {
		Book book = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetBookById = con.prepareStatement("SELECT * FROM BOOK WHERE BOOK.ID = ? ")) {
			stmtGetBookById.setLong(1, id);
			try (ResultSet results = stmtGetBookById.executeQuery()) {
				while (results.next()) {
					book = new Book();
					book.setId(results.getLong("id"));
					book.setName(results.getString("name"));
					book.setDescription(results.getString("description"));
					book.setProcessingOrg(LegalEntitySQL.getLegalEntityById(results.getLong("processing_org_id")));
				}
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return book;
	}

	public static boolean bookExists(String name, long poId) {
		boolean exists = false;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtBookExists = con
						.prepareStatement("SELECT 1 FROM BOOK WHERE BOOK.NAME = ? AND PROCESSING_ORG_ID = ?")) {
			stmtBookExists.setString(1, name);
			stmtBookExists.setLong(2, poId);
			try (ResultSet results = stmtBookExists.executeQuery()) {
				while (results.next()) {
					exists = true;
				}
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return exists;
	}

	public static Set<Book> getAllBooks() {
		Set<Book> books = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetAllBooks = con.prepareStatement("SELECT * FROM BOOK");
				ResultSet results = stmtGetAllBooks.executeQuery()) {
			while (results.next()) {
				Book book = new Book();
				book.setId(results.getLong("id"));
				book.setName(results.getString("name"));
				book.setDescription(results.getString("description"));
				book.setProcessingOrg(LegalEntitySQL.getLegalEntityById(results.getLong("processing_org_id")));
				if (books == null) {
					books = new HashSet<Book>();
				}
				books.add(book);
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return books;
	}

	public static long saveBook(Book book) {
		long bookId = 0;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtSaveBook = (book.getId() == 0)
						? con.prepareStatement(
								"INSERT INTO BOOK(NAME, DESCRIPTION, PROCESSING_ORG_ID) VALUES (?, ?, ?) ",
								Statement.RETURN_GENERATED_KEYS)
						: con.prepareStatement(
								"UPDATE BOOK SET NAME=?, DESCRIPTION=?, PROCESSING_ORG_ID=? WHERE ID=?")) {
			if (book.getId() != 0) {
				stmtSaveBook.setLong(4, book.getId());
			}
			stmtSaveBook.setString(1, book.getName());
			stmtSaveBook.setString(2, book.getDescription());
			stmtSaveBook.setLong(3, book.getProcessingOrg().getId());
			stmtSaveBook.executeUpdate();
			if (book.getId() == 0) {
				try (ResultSet generatedKeys = stmtSaveBook.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						bookId = generatedKeys.getLong(1);
					} else {
						throw new SQLException("Creating book failed, no generated key obtained.");
					}
				}
			} else {
				bookId = book.getId();
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		book.setId(bookId);
		return bookId;
	}

	public static Book getBookByName(String name) {
		Book book = null;
		try (Connection con = TradistaDB.getConnection();
				PreparedStatement stmtGetBookByName = con.prepareStatement("SELECT * FROM BOOK WHERE NAME = ? ")) {
			stmtGetBookByName.setString(1, name);
			try (ResultSet results = stmtGetBookByName.executeQuery()) {
				while (results.next()) {
					book = new Book();
					book.setId(results.getLong("id"));
					book.setName(results.getString("name"));
					book.setDescription(results.getString("description"));
					book.setProcessingOrg(LegalEntitySQL.getLegalEntityById(results.getLong("processing_org_id")));
				}
			}
		} catch (SQLException sqle) {
			// TODO Manage logs
			sqle.printStackTrace();
			throw new TradistaTechnicalException(sqle);
		}
		return book;
	}

}