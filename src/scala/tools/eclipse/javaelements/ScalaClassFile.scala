/*
 * Copyright 2005-2009 LAMP/EPFL
 */
// $Id$

package scala.tools.eclipse.javaelements

import java.util.{ HashMap => JHashMap }

import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.IStatus
import org.eclipse.jdt.core.{
  ICompilationUnit, IJavaElement, IPackageDeclaration, IProblemRequestor, IJavaModelStatusConstants, IType, JavaModelException,
  WorkingCopyOwner }
import org.eclipse.jdt.core.compiler.CharOperation
import org.eclipse.jdt.internal.compiler.env
import org.eclipse.jdt.internal.compiler.env.IBinaryType
import org.eclipse.jdt.internal.core.{
  BasicCompilationUnit, BinaryType, ClassFile, DefaultWorkingCopyOwner, JavaModelStatus, JavaProject, PackageFragment }
import org.eclipse.jdt.internal.core.util.Util

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jdt.core.dom.CompilationUnit

import scala.tools.nsc.io.{ AbstractFile, VirtualFile }

import scala.tools.eclipse.contribution.weaving.jdt.IScalaClassFile

class ScalaClassFile(parent : PackageFragment, name : String, sourceFile : String)
  extends ClassFile(parent, name) with ScalaCompilationUnit with IScalaClassFile {
  override def replacementImage = ScalaImages.SCALA_CLASS_FILE

  override def getElementAt(position : Int) : IJavaElement = {
    val e = getSourceElementAt(position)
    if (e == this) null else e
  }

  def getCorrespondingElement(element : IJavaElement) : Option[IJavaElement] = {
    val name = element.getElementName
    getChildren.find(_.getElementName == name)
  }
  
  override def codeSelect(offset : Int, length : Int, owner : WorkingCopyOwner) : Array[IJavaElement] =
    codeSelect(this, offset, length, owner)
  
  def getContents() = getBuffer.getCharacters
    
  def getFile : AbstractFile = new VirtualFile(getSourceFileName)
  
  def getSourceFileName() = sourceFile
  
  def getSourceFilePath() = {
    val tpe = getType
    val pkgFrag = tpe.getPackageFragment.asInstanceOf[PackageFragment]
    Util.concatWith(pkgFrag.names, sourceFile, '/')
  }

  def getPackageName() : Array[Array[Char]] = {
    val packageFragment = getParent.asInstanceOf[PackageFragment]
    if (packageFragment == null)
      CharOperation.NO_CHAR_CHAR
    else
      Util.toCharArrays(packageFragment.names)
  }

  override def getType() : IType = {
    val tpe = super.getType()
    getCorrespondingElement(tpe).getOrElse(tpe).asInstanceOf[IType]
  }
  
  def getMainTypeName() : Array[Char] =
    Util.getNameWithoutJavaLikeExtension(getElementName).toCharArray

  override def getTypeName() : String = {
    val lastDollar = name.lastIndexOf('$')
    if(lastDollar == -1 || lastDollar != name.length-1)
      super.getTypeName
    else {
      val lastDollar0 = name.lastIndexOf('$', lastDollar-1) 
      if (lastDollar0 > -1) Util.localTypeName(name, lastDollar0, name.length()) else name
    }
  }

  def getFileName() : Array[Char] =
    getPath.toString.toCharArray
    
  def closeBuffer0() = super.closeBuffer()
  def closing0(info : AnyRef) = super.closing(info)
  def createElementInfo0() = super.createElementInfo()
  def generateInfos0(info : AnyRef, newElements : JHashMap[_, _], monitor : IProgressMonitor) =
    super.generateInfos(info, newElements, monitor)
  def getBufferManager0() = super.getBufferManager()
  def hasBuffer0() : Boolean = super.hasBuffer()
  def openBuffer0(pm : IProgressMonitor, info : Object) = super.openBuffer(pm, info)
  def resourceExists0(underlyingResource : IResource) = super.resourceExists(underlyingResource) 
  def validateExistence0(underlyingResource : IResource) : IStatus = super.validateExistence(underlyingResource)
  def openAncestors0(newElements : JHashMap[_, _], monitor : IProgressMonitor) { super.openAncestors(newElements, monitor) }
  def getHandleMementoDelimiter0() = super.getHandleMementoDelimiter()
  def isSourceElement0() : Boolean = super.isSourceElement()
}