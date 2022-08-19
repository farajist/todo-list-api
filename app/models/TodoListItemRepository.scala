package models
import slick.jdbc.H2Profile.api._

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TodoListItemTable(tag: Tag) extends Table[TodoListItem](tag, "todo_items") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def description = column[String]("description")

  def isDone = column[Boolean]("is_done")

  def * = (id, description, isDone) <> ((TodoListItem.apply _).tupled, TodoListItem.unapply)
}


class TodoListItemRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  val todos = TableQuery[TodoListItemTable]

  def create(description: String): Future[TodoListItem] = db.run {
    (todos.map(t => (t.description, t.isDone))
      returning todos.map(_.id)
      into ((descriptionState, id) => TodoListItem(id, descriptionState._1, descriptionState._2))
      ) += (description, false)
  }

  def list(): Future[Seq[TodoListItem]] = db.run {
    todos.result
  }

  def get(id: Long): Future[Option[TodoListItem]] = db.run {
    todos.filter{ _.id === id }.take(1).result.headOption
  }

  def updateStatus(id: Long, isDone: Boolean = true) = db.run {
    todos.filter {_.id === id }.map(_.isDone).update(isDone)
  }

  def remove(id: Long) = db.run {
    todos.filter { _.id === id }.delete
  }

  def removeAllDone = db.run {
    todos.filter { _.isDone }.delete
  }
}
