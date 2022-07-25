package controllers

import models.{NewTodoListItem, TodoListItem, TodoListItemRepository}
import play.api.libs.json.Json
import play.api.mvc.{ MessagesControllerComponents, MessagesAbstractController}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TodoListController @Inject()(repo: TodoListItemRepository,
                                   cc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
//  private val todoList = new mutable.ListBuffer[TodoListItem]()
//  todoList += TodoListItem(1, "a first task", true)
//  todoList += TodoListItem(2, "some other task", false)

  implicit val todoListJson = Json.format[TodoListItem]
  implicit val newTodoListJson = Json.format[NewTodoListItem]


  def getAll = Action.async {
    repo.list().map { todos =>
      if (todos.isEmpty) NoContent else Ok(Json.toJson(todos))
    }
  }

  def getById(itemId: Long) = Action.async {
    val foundItem = repo.get(itemId)
    foundItem map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def addNewItem = Action.async { request =>
    val content = request.body
    val jsonObject = content.asJson

    val todoListItem: Option[NewTodoListItem] = jsonObject.flatMap(Json.fromJson[NewTodoListItem](_).asOpt)

    todoListItem match {
      case None => Future(BadRequest)
      case Some(newItem) =>
//        val nextId = todoList.map(_.id).max + 1
//        val toBeAdded = TodoListItem(nextId, newItem.description, false)
//        todoList += toBeAdded
        repo.create(newItem.description).map { item => Created(Json.toJson(item)) }
//
    }
  }

  def markAsDone(itemId: Long) = Action.async {
//    val foundItem = todoList.find(_.id == itemId)
//    foundItem match {
//      case None => NotFound
//      case Some(item) =>
//        val newItem = item.copy(isDone = true)
//        todoList -= item
//        todoList += newItem
//        Accepted(Json.toJson(newItem))
//
//    }
  repo.updateStatus(itemId).map { updatedRows: Int =>
      if (updatedRows == 0) NotFound else Accepted
    }
  }

  def removeItem(itemId: Long) = Action.async {
//   todoList.filterInPlace(_.id != itemId)
    repo.remove(itemId).map { rows =>
      if (rows == 0) NotFound else NoContent
    }
  }

  def removeAllDone = Action.async {
//    todoList.filterInPlace(_.isDone == false)
    repo.removeAllDone.map { _ => Accepted }

  }
}
